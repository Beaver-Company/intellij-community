package com.jetbrains.python.testing;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.NonDefaultProjectConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;

import javax.swing.*;
import java.util.List;

/**
 * User: catherine
 */
public class TestRunnerConfigurable implements Configurable, NonDefaultProjectConfigurable {
  private JPanel myMainPanel;
  private JComboBox myComboBox;
  private PythonTestConfigurationsModel myModel;
  private Project myProject;

  public TestRunnerConfigurable(Project project) {
    myProject = project;
  }
  @Nls
  @Override
  public String getDisplayName() {
    return "Test Runner";
  }

  @Override
  public Icon getIcon() {
    return null;
  }

  @Override
  public String getHelpTopic() {
    return "test_runner_configuration";
  }

  @Override
  public JComponent createComponent() {
    List<String> configurations = TestRunnerService.getInstance(myProject).getConfigurations();
    myModel = new PythonTestConfigurationsModel(configurations, TestRunnerService.getInstance(myProject).getProjectConfiguration(),
                                                myProject);
    updateConfigurations();
    return myMainPanel;
  }

  private void updateConfigurations() {
    myComboBox.setModel(myModel);

  }

  @Override
  public boolean isModified() {
    if (myComboBox.getSelectedItem() != myModel.getProjectConfiguration()) {
      return true;
    }
    return false;
  }

  @Override
  public void apply() throws ConfigurationException {
    myModel.apply();
  }

  @Override
  public void reset() {
    myComboBox.setSelectedItem(myModel.getProjectConfiguration());
    myComboBox.repaint();
    myModel.reset();
  }

  @Override
  public void disposeUIResources() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
