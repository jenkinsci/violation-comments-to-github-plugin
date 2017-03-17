package org.jenkinsci.plugins.jvctg.config;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

import se.bjurr.violations.lib.reports.Reporter;

public class ViolationConfig implements Serializable {
  private static final long serialVersionUID = 9009372864417543781L;

  private String pattern;
  private Reporter reporter;

  public ViolationConfig() {}

  @DataBoundConstructor
  public ViolationConfig(Reporter reporter, String pattern) {
    this.reporter = reporter;
    this.pattern = pattern;
  }

  public String getPattern() {
    return this.pattern;
  }

  public Reporter getReporter() {
    return this.reporter;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public void setReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public String toString() {
    return "ViolationConfig [pattern=" + this.pattern + ", reporter=" + this.reporter + "]";
  }
}
