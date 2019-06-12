/**
 * Copyright (c) Connexta
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package com.connexta.replication.docker.tests;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ITDockerTest {

  private String testHostOne;
  private String testHostTwo;

  @ClassRule
  public static GenericContainer ddfOne =
      new GenericContainer<>("codice/ddf:2.15.0").withExposedPorts(8993);

  //  @ClassRule
  //  public static GenericContainer ddfTwo =
  //      new GenericContainer<>("codice/ddf:2.15.0").withExposedPorts(8993);

  @Before
  public void beforeTest() throws IOException, InterruptedException {
    System.out.println("installing Profiles");
    testHostOne = installProfile(ddfOne);
    //    testHostTwo = installProfile(ddfTwo);
    System.out.println("profiles installed");
    // need to ingest products?
  }

  private String installProfile(GenericContainer ddf) throws IOException, InterruptedException {
    System.out.println("waiting for admin");
    ddf.waitingFor(Wait.forHttps("/admin").forStatusCode(302));
    System.out.println("calling  install command");
    ddf.execInContainer("/app/bin/client profile:install standard");
    System.out.println(ddf.getLogs());
    ddf.waitingFor(Wait.forLogMessage("Installation Complete", 1));
    return ddf.getContainerIpAddress() + ":" + ddf.getMappedPort(8993);
  }

  @Test
  public void testSimpleRequest() {
    System.out.println(testHostOne);
    assertThat(testHostOne.contains("localhost"), is(true));

    //    System.out.println(testHostTwo);
    //    assertThat(testHostTwo.contains("localhost"), is(true));

  }

  //  @Test
  //  public void testProductsAreIngested() {
  //    //ddf1 should have all three resources and ddf2 should be "empty"
  //    RequestSpecBuilder builder = new RequestSpecBuilder();
  //    builder.addHeader("Content-Type", "multipart/mixed");
  //    builder.addFormParam("parse.metadata", "@./resources/hamlet.pdf");
  //
  //    RequestSpecification requestSpecification = builder.build();
  //
  //    given().spec(requestSpecification).config(RestAssured.config().encoderConfig(encoderConfig()
  //            .encodeContentTypeAs("application/json;charset=UTF-8", ContentType.JSON)))
  //    .when().post(testHostOne + "/services/catalog");
  //
  //    ddfOne.waitingFor(Wait.forLogMessage("Never Ever Gonna Happen",2));
  //  }
}
