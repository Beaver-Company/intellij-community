/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.groovy.console.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.plugins.groovy.console.GroovyConsole;
import org.jetbrains.plugins.groovy.lang.psi.GroovyFile;
import org.jetbrains.plugins.groovy.lang.psi.api.toplevel.imports.GrImportStatement;

public class GrExecuteCommandAction extends AnAction {

  public GrExecuteCommandAction() {
    super(AllIcons.Toolwindows.ToolWindowRun);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Project project = e.getProject();
    final Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());
    final VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    if (project == null || editor == null || virtualFile == null) return;

    FileDocumentManager.getInstance().saveAllDocuments();

    final Document document = editor.getDocument();
    final TextRange selectedRange = EditorUtil.getSelectionInAnyMode(editor);
    final String command;
    if (selectedRange.isEmpty()) {
      command = document.getText(); // whole document
    }
    else {
      StringBuilder commandBuilder = new StringBuilder();
      PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
      if (file instanceof GroovyFile) {
        GrImportStatement[] statements = ((GroovyFile)file).getImportStatements();
        for (GrImportStatement statement : statements) {
          if (!selectedRange.contains(statement.getTextRange())) {
            commandBuilder.append(statement.getText()).append("\n");
          }
        }
      }
      commandBuilder.append(document.getText(selectedRange));
      command = commandBuilder.toString();
    }

    final GroovyConsole existingConsole = virtualFile.getUserData(GroovyConsole.GROOVY_CONSOLE);
    if (existingConsole == null) {
      GroovyConsole.getOrCreateConsole(project, virtualFile, console -> console.execute(command));
    }
    else {
      existingConsole.execute(command);
    }
  }
}
