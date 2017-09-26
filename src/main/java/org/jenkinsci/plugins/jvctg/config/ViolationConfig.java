package org.jenkinsci.plugins.jvctg.config;

import java.io.Serializable;

import org.kohsuke.stapler.DataBoundConstructor;

import se.bjurr.violations.lib.reports.Parser;

public class ViolationConfig implements Serializable {
  private static final long serialVersionUID = 9009372864417543781L;

  private String pattern;
  private String reporter;
  private Parser parser;

  public ViolationConfig() {}

  @DataBoundConstructor
  public ViolationConfig(Parser parser, String reporter, String pattern) {
    this.reporter = reporter;
    this.parser = parser;
    this.pattern = pattern;
  }

  public String getPattern() {
    return this.pattern;
  }

  public String getReporter() {
	  if (this.reporter == null) {
		return this.parser.name();
	}
    return this.reporter;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public Parser getParser() {
    return parser;
  }

  public void setParser(Parser parser) {
    this.parser = parser;
  }

  @Override
  public String toString() {
    return "ViolationConfig [pattern="
        + pattern
        + ", reporter="
        + reporter
        + ", parser="
        + parser
        + "]";
  }
}
