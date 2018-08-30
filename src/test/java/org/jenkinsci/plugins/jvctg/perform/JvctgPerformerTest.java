package org.jenkinsci.plugins.jvctg.perform;

import static org.junit.Assert.assertEquals;

import hudson.EnvVars;
import org.jenkinsci.plugins.jvctg.config.ViolationsToGitHubConfig;
import org.junit.Test;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class JvctgPerformerTest {

  @Test
  public void testThatAllVariablesAreExpanded() {
    ViolationsToGitHubConfig original =
        new PodamFactoryImpl().manufacturePojo(ViolationsToGitHubConfig.class);

    ViolationsToGitHubConfig expanded =
        JvctgPerformer.expand(
            original,
            new EnvVars() {
              private static final long serialVersionUID = 3950742092801432326L;

              @Override
              public String expand(String s) {
                return s;
              }
            });

    assertEquals(original.toString(), expanded.toString());
  }
}
