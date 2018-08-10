package org.eclipse.leshan.client.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PolySenseSensor extends BaseInstanceEnabler {

  private static final int SENSOR_VALUE = 1212;
  private static final List<Integer> supportedResources = Arrays.asList(SENSOR_VALUE);
  private final ScheduledExecutorService scheduler;
  private final Random rng = new Random();

  private static final Logger LOG = LoggerFactory.getLogger(PolySenseSensor.class);

  private String value;

  public PolySenseSensor() {
    this.scheduler =
        Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Polysense Sensor"));
    scheduler.scheduleAtFixedRate(
        new Runnable() {

          @Override
          public void run() {
            generateNewValue();
            fireResourcesChange(SENSOR_VALUE);
          }
        },
        2,
        1,
        TimeUnit.SECONDS);
  }

  @Override
  public ReadResponse read(int resourceId) {
    switch (resourceId) {
      case SENSOR_VALUE:
        LOG.info("READ value=" + value);
        return ReadResponse.success(resourceId, value);
      default:
        return super.read(resourceId);
    }
  }

  private synchronized String generateNewValue() {
    int x = rng.nextInt(2000);
    int y = rng.nextInt(2000) - 2000;
    int z = rng.nextInt(2000);

    this.value = new StringBuilder().append(x).append(";").append(y).append(";").append(z).toString();

    return this.value;
  }
}
