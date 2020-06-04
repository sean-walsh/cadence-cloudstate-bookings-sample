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

import akka.actor.ActorSystem;
import akka.grpc.GrpcClientSettings;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import carservice.*;
import carservice.Carservice.*;
import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowException;
import com.uber.cadence.worker.Worker;
import flightservice.*;
import flightservice.Flightservice.*;
import hotelservice.*;
import hotelservice.Hotelservice.*;

import java.util.UUID;

import static com.uber.cadence.samples.common.SampleConstants.DOMAIN;

public class TripBookingSaga {

    static final String TASK_LIST = "TripBooking";

    private static final ActorSystem system = ActorSystem.create("BookingsServiceClient");
    private static final Materializer materializer = ActorMaterializer.create(system);
    // NOTE: The cloudstate bookings ip/port are subject to change.

    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) {
        // Get worker to poll the common task list.
        Worker.Factory factory = new Worker.Factory(DOMAIN);
        final Worker workerForCommonTaskList = factory.newWorker(TASK_LIST);
        workerForCommonTaskList.registerWorkflowImplementationTypes(TripBookingWorkflowImpl.class);
        GrpcClientSettings settings = GrpcClientSettings.connectToServiceAt("192.168.99.117", 31923, system);
        CarBookingServiceClient carBookingServiceClient = CarBookingServiceClient.create(settings, materializer, system.dispatcher());
        HotelBookingServiceClient hotelBookingServiceClient = HotelBookingServiceClient.create(settings, materializer, system.dispatcher());
        FlightBookingServiceClient flightBookingServiceClient = FlightBookingServiceClient.create(settings, materializer, system.dispatcher());
        TripBookingActivities tripBookingActivities = new TripBookingActivitiesImpl(
                carBookingServiceClient, hotelBookingServiceClient, flightBookingServiceClient);
        workerForCommonTaskList.registerActivitiesImplementations(tripBookingActivities);

        // Start all workers created by this factory.
        factory.start();
        System.out.println("Worker started for task list: " + TASK_LIST);

        WorkflowClient workflowClient = WorkflowClient.newInstance(DOMAIN);

        // now we can start running instances of our saga - its state will be persisted
        TripBookingWorkflow trip1 = workflowClient.newWorkflowStub(TripBookingWorkflow.class);
        try {
            ReserveCarCommand reserveCarCommand = Carservice.ReserveCarCommand.newBuilder()
                    .setReservationId(UUID.randomUUID().toString())
                    .setUserId("SeanW")
                    .setCompany("Hertz")
                    .setCarType("midsize")
                    .build();

            ReserveHotelCommand reserveHotelCommand = Hotelservice.ReserveHotelCommand.newBuilder()
                    .setReservationId(UUID.randomUUID().toString())
                    .setUserId("SeanW")
                    .setHotel("Marriott")
                    .setRoomNumber("555")
                    .build();

            ReserveFlightCommand reserveFlightCommand = Flightservice.ReserveFlightCommand.newBuilder()
                    .setReservationId(UUID.randomUUID().toString())
                    .setUserId("SeanW")
                    .setFlightNumber("UA977")
                    .build();

            trip1.bookTrip(reserveCarCommand, reserveHotelCommand, reserveFlightCommand);
        } catch (WorkflowException e) {
            // Expected
            e.printStackTrace();
        }

        System.exit(0);
    }
}
