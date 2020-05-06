/*
 *  Copyright 2012-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Modifications copyright (C) 2017 Uber Technologies, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"). You may not
 *  use this file except in compliance with the License. A copy of the License is
 *  located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 *  or in the "license" file accompanying this file. This file is distributed on
 *  an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package com.uber.cadence.samples.bookingsaga;

import com.uber.cadence.activity.ActivityOptions;
import com.uber.cadence.workflow.ActivityException;
import com.uber.cadence.workflow.Saga;
import com.uber.cadence.workflow.Workflow;
import java.time.Duration;

import carservice.Carservice.*;
import hotelservice.Hotelservice.*;
import flightservice.Flightservice.*;

public class TripBookingWorkflowImpl implements TripBookingWorkflow {

  private final ActivityOptions options =
      new ActivityOptions.Builder().setScheduleToCloseTimeout(Duration.ofHours(1)).build();
  private final TripBookingActivities activities =
      Workflow.newActivityStub(TripBookingActivities.class, options);

  @Override
  public void bookTrip(ReserveCarCommand reserveCarCommand, ReserveHotelCommand reserveHotelCommand,
                       ReserveFlightCommand reserveFlightCommand) {
    // Configure SAGA to run compensation activities in parallel
    Saga.Options sagaOptions = new Saga.Options.Builder().setParallelCompensation(true).build();
    Saga saga = new Saga(sagaOptions);
    try {
      activities.reserveCar(reserveCarCommand);
      saga.addCompensation(activities::cancelCar, CancelCarReservationCommand.newBuilder()
      .setReservationId(reserveCarCommand.getReservationId()).build());

      activities.reserveHotel(reserveHotelCommand);
      saga.addCompensation(activities::cancelHotel, CancelHotelReservationCommand.newBuilder()
              .setReservationId(reserveHotelCommand.getReservationId()).build());

      activities.reserveFlight(reserveFlightCommand);
      saga.addCompensation(activities::cancelFlight, CancelFlightReservationCommand.newBuilder()
              .setReservationId(reserveFlightCommand.getReservationId()).build());
    } catch (ActivityException e) {
      saga.compensate();
      throw e;
    }
  }
}
