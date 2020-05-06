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

import carservice.Carservice.*;
import flightservice.Flightservice.*;
import hotelservice.Hotelservice.*;

import static com.uber.cadence.samples.bookingsaga.TripBookingSaga.*;

public class TripBookingActivitiesImpl implements TripBookingActivities {
  @Override
  public String reserveCar(ReserveCarCommand reserveCarCommand) {
    System.out.println("reserve car for '" + reserveCarCommand.getUserId() + "'");
    carBookingServiceClient.reserveCar(reserveCarCommand);
    return reserveCarCommand.getReservationId();
  }

  @Override
  public String reserveFlight(ReserveFlightCommand reserveFlightCommand) {
    System.out.println("failing to book flight for '" + reserveFlightCommand.getUserId() + "'");
    throw new RuntimeException("Flight booking did not work");
  }

  @Override
  public String reserveHotel(ReserveHotelCommand reserveHotelCommand) {
    System.out.println("booking hotel for '" + reserveHotelCommand.getUserId() + "'");
    hotelBookingServiceClient.reserveHotel(reserveHotelCommand);
    return reserveHotelCommand.getReservationId();
  }

  @Override
  public String cancelFlight(CancelFlightReservationCommand cancelFlightReservationCommand) {
    System.out.println("cancelling flight reservation '" + cancelFlightReservationCommand.getReservationId() + "'");
    return "";
  }

  @Override
  public String cancelHotel(CancelHotelReservationCommand cancelHotelReservationCommand) {
    System.out.println("cancelling hotel reservation '" + cancelHotelReservationCommand.getReservationId() + "'");
    return cancelHotelReservationCommand.getReservationId();
  }

  @Override
  public String cancelCar(CancelCarReservationCommand cancelCarReservationCommand) {
    System.out.println("cancelling car reservation '" + cancelCarReservationCommand.getReservationId() + "'");
    return cancelCarReservationCommand.getReservationId();
  }
}
