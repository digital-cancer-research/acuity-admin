package com.acuity.visualisations.batch.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.acuity.visualisations.web.entity.JobExecutionEvent;
import com.acuity.visualisations.web.service.listener.EtlAllEventListener;

@Component
public class SchedulerSingletonStorage implements InitializingBean, Observer {

	private String receivedEventGuid;

	private static class EventQueueCleaner implements Runnable {
		private static final Logger LOGGER = LoggerFactory.getLogger(Runnable.class);
		
		private static final int REDUCTION_STEP_SIZE = 200;
		private static final int EVENT_QUEUE_MAX_SIZE = 400;
		private Map<String, JobExecutionEvent> events;

		public EventQueueCleaner(Map<String, JobExecutionEvent> events) {
			this.events = events;
		}

		public void run() {
			synchronized (events) {
				while (events.size() < EVENT_QUEUE_MAX_SIZE) {
					try {
						events.wait();
					} catch (InterruptedException e) {
						LOGGER.error(e.getMessage());
					}
				}
				Iterator<String> keyIterator = events.keySet().iterator();
				int i = 0;
				while (keyIterator.hasNext() && i < REDUCTION_STEP_SIZE) {
					keyIterator.next();
					keyIterator.remove();
					i++;
				}
			}

		}

	}

	private Map<String, JobExecutionEvent> events = Collections.synchronizedMap(new LinkedHashMap<String, JobExecutionEvent>());
	private Thread cleaner = new Thread(new EventQueueCleaner(events));

	@Autowired
	private EtlAllEventListener manager;

	@Override
	public void afterPropertiesSet() throws Exception {
		manager.addObserver(this);
		cleaner.setDaemon(true);
		cleaner.start();
	}

	@Override
	public void update(Observable o, Object arg) {
		JobExecutionEvent event = (JobExecutionEvent) arg;
		String key = event.getExecutionId();
		synchronized (events) {
			events.put(key, event);
			events.notifyAll();
		}
	}

	public JobExecutionEvent getEvent() throws Exception {
		JobExecutionEvent event = null;
		synchronized (events) {
			while (event == null) {
				Iterator<String> i = events.keySet().iterator();
				if (receivedEventGuid == null && i.hasNext()) {
					String key = i.next();
					event = events.get(key);
				} else {
					boolean presented = false;
					while (i.hasNext()) {
						String key = i.next();
						if (key.equals(receivedEventGuid)) {
							presented = true;
							if (i.hasNext()) {
								event = events.get(i.next());
								break;
							}
						}
					}
					if (!presented) {
						i = events.keySet().iterator();
						if (i.hasNext()) {
							String key = i.next();
							event = events.get(key);
						}
					}
				}
				if (event == null) {
					events.wait();
				}
			}
		}
		return event;
	}

	public void saveState(String eventId) {
		receivedEventGuid = eventId;
	}
}
