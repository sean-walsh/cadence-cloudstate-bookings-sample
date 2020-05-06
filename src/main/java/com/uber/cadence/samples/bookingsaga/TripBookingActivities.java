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

public interface TripBookingActivities {

  /**
   * @param reserveCarCommand the command for reserving a car
   * @return reservationID
   */
  String reserveCar(ReserveCarCommand reserveCarCommand);

  /**
   * @param reserveFlightCommand ReserveFlightCommand
   * @return reservationID
   */
  String reserveFlight(ReserveFlightCommand reserveFlightCommand);

  /**
   * @param reserveHotelCommand ReserveHotelCommand
   * @return reservationID
   */
  String reserveHotel(ReserveHotelCommand reserveHotelCommand);

  /**
   * @param cancelFlightReservationCommand CancelFlightReservationCommand
   * @return cancellationConfirmationID
   */
  String cancelFlight(CancelFlightReservationCommand cancelFlightReservationCommand);

  /**
   * @param cancelHotelReservationCommand CancelHotelReservationCommand
   * @return cancellationConfirmationID
   */
  String cancelHotel(CancelHotelReservationCommand cancelHotelReservationCommand);

  /**
   * @param cancelCarReservationCommand CancelCarReservationCommand
   * @return cancellationConfirmationID
   */
  String cancelCar(CancelCarReservationCommand cancelCarReservationCommand);
}
