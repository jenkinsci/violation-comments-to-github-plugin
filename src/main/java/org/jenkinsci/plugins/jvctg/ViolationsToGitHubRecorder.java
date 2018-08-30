package org.jenkinsci.plugins.jvctg;

import static hudson.tasks.BuildStepMonitor.NONE;
import static org.jenkinsci.plugins.jvctg.perform.JvctgPerformer.jvctsPerform;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfig;
import org.kohsuke.stapler.DataBoundConstructor;

public class ViolationsToGitHubRecorder extends Recorder implements SimpleBuildStep {

  public static final BuildStepDescriptor<Publisher> DESCRIPTOR =
      new ViolationsToGitHubDescriptor();

  private ViolationsToGitHubConfig config;

  public ViolationsToGitHubRecorder() {}

  @DataBoundConstructor
  public ViolationsToGitHubRecorder(final ViolationsToGitHubConfig config) {
    this.config = config;
  }

  public ViolationsToGitHubConfig getConfig() {
    return this.config;
  }

  @Override
  public BuildStepDescriptor<Publisher> getDescriptor() {
    return DESCRIPTOR;
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return NONE;
  }

  @Override
  public void perform(
      @NonNull final Run<?, ?> build,
      @NonNull final FilePath filePath,
      @NonNull final Launcher launcher,
      @NonNull final TaskListener listener)
      throws InterruptedException, IOException {

    final ViolationsToGitHubConfig combinedConfig = new ViolationsToGitHubConfig(this.config);
    final ViolationsToGitHubConfiguration defaults = ViolationsToGitHubConfiguration.get();

    combinedConfig.applyDefaults(defaults);

    jvctsPerform(combinedConfig, filePath, build, listener);
  }

  public void setConfig(final ViolationsToGitHubConfig config) {
    this.config = config;
  }
}
