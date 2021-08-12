/*
 * Copyright (c) 2008-2021, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.hackaton.cpuintensive;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/cpu")
public class CpuEndpoint {

    private static final AtomicBoolean BREAK = new AtomicBoolean();

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostMapping("/stop")
    public void stop() {
        BREAK.set(true);
    }

    @PostMapping("/start")
    public String start() {
        IMap<String, Long> metrics = hazelcastInstance.getMap("requests");
        metrics.setAsync("cpu-" + UUID.randomUUID(), System.currentTimeMillis());
        Random random = new Random();
        for (int i = 0; i <= 1_000_000; i++) {
            if (BREAK.get()) {
                break;
            }
            double sqrt = Math.sqrt(random.nextInt(1_000));
        }
        return "Ok";
    }
}
