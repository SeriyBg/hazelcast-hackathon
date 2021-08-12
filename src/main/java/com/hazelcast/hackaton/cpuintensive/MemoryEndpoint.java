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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/memory")
public class MemoryEndpoint {

    private static final AtomicLong SLEEP_FOR = new AtomicLong(1_000);
    private static final AtomicBoolean BREAK = new AtomicBoolean();

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @PostMapping("/sleep")
    public void setSleepFor(@RequestBody Long sleepFor) {
        SLEEP_FOR.set(sleepFor);
    }

    @PostMapping("/stop")
    public void stop() {
        BREAK.set(true);
    }

    @PostMapping("/start")
    public String start(@RequestHeader("x-sleep-for") Long sleepFor) {
        IMap<String, Long> metrics = hazelcastInstance.getMap("requests");
        metrics.setAsync("memory-" + UUID.randomUUID(), System.currentTimeMillis());
        if (sleepFor != null) {
            SLEEP_FOR.set(sleepFor);
        }
        var array = new String[1_000_000];
        for (int i = 0; i < 1_000_000; i++) {
            array[i] = Thread.currentThread().getName() + System.currentTimeMillis();
            if (BREAK.get()) {
                break;
            }
        }
        try {
            Thread.sleep(SLEEP_FOR.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Ok";
    }
}
