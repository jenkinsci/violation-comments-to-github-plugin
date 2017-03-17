package org.jenkinsci.plugins.jvctg.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import uk.co.jemos.podam.api.PodamFactoryImpl;

public class ViolationsToGitHubConfigTest {

  @Test
  public void testThatCopyConstructorCopiesEverything() {
    ViolationsToGitHubConfig original =
        new PodamFactoryImpl().manufacturePojo(ViolationsToGitHubConfig.class);
    ViolationsToGitHubConfig actual = new ViolationsToGitHubConfig(original);
    assertEquals(original, actual);
  }
}
