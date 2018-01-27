package org.jenkinsci.plugins.jvctg;

import static org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfigHelper.FIELD_MINSEVERITY;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfig;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;

@Extension
@Symbol("ViolationsToGitHub")
public final class ViolationsToGitHubDescriptor extends BuildStepDescriptor<Publisher> {
  private ViolationsToGitHubConfig config;

  public ViolationsToGitHubDescriptor() {
    super(ViolationsToGitHubRecorder.class);
    load();
    if (this.config == null) {
      this.config = new ViolationsToGitHubConfig();
    }
  }

  @Override
  public String getDisplayName() {
    return "Report Violations to GitHub";
  }

  @Override
  public String getHelpFile() {
    return super.getHelpFile();
  }

  @Override
  public boolean isApplicable(
      @SuppressWarnings("rawtypes") final Class<? extends AbstractProject> jobType) {
    return true;
  }

  @Override
  public Publisher newInstance(final StaplerRequest req, final JSONObject formData)
      throws hudson.model.Descriptor.FormException {
    if (formData != null) {
      final JSONObject config = formData.getJSONObject("config");
      final String minSeverity = config.getString(FIELD_MINSEVERITY);
      if (StringUtils.isBlank(minSeverity)) {
        config.remove(FIELD_MINSEVERITY);
      }
    }

    return req.bindJSON(ViolationsToGitHubRecorder.class, formData);
  }
}
