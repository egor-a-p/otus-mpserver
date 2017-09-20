package ru.otus.transport.cdi;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

import ru.otus.common.cdi.Startup;
import ru.otus.common.cdi.event.Transport;
import ru.otus.common.protocol.Message;
import ru.otus.transport.api.Connection;
import ru.otus.transport.api.ConnectionManager;
import ru.otus.transport.api.Destination;
import ru.otus.transport.api.Source;
import ru.otus.transport.api.TransportProcessor;
import ru.otus.transport.api.TransportState;

@Startup
@ApplicationScoped
public class TransportBean {
	private static final long DELAY = TimeUnit.SECONDS.toMillis(5);
	private static final long PERIOD = TimeUnit.SECONDS.toMillis(5);

	@Inject
	private ConnectionManager<Message> connectionManager;
	@Inject
	private Destination<Message> destination;
	@Inject
	private Source<Message> source;
	@Inject @Transport
	private Event<TransportState> transportEvent;
	@Inject
	private Logger logger;

	private Timer timer = new Timer();
	private TransportProcessor<Message> transportProcessor;

	@PostConstruct
	public void init() {
		logger.debug("Init transport...");
		transportProcessor = new TransportProcessor<>(source, destination);
		timer.schedule(new ConnectionTask(), DELAY, PERIOD);
	}

	private void onStart() {
		timer.purge();
		transportEvent.fireAsync(TransportState.STARTED);
		logger.debug("On start transport notify observers and stop connection task...");
	}

	private void onStop() {
		transportEvent.fireAsync(TransportState.STOPPED);
		timer.schedule(new ConnectionTask(), DELAY, PERIOD);
		logger.debug("On stop transport notify observers and schedule connection task...");
	}

	private void close() {
		try {
			source.close();
			destination.close();
		} catch (Exception e) {
			logger.error("Exception on close: ", e);
		}
	}

	@PreDestroy
	public void destroy() {
		logger.debug("Destroy transport...");
		transportProcessor.shutdown();
		timer.cancel();
		close();
	}

	private class ConnectionTask extends TimerTask {
		@Override
		public void run() {
			try {
				Connection<Message> connection = connectionManager.getConnection();
				transportProcessor.start(connection, TransportBean.this::onStop, source::clear);
				cancel();
				onStart();
			} catch (Exception e) {
				logger.warn("Can't start transport: ", e);
			}
		}
	}
}
