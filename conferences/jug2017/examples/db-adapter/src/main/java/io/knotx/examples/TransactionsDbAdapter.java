package io.knotx.examples;

import io.knotx.examples.impl.TransactionsDbAdapterProxyImpl;
import io.knotx.proxy.AdapterProxy;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ProxyHelper;

public class TransactionsDbAdapter extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionsDbAdapter.class);

  private MessageConsumer<JsonObject> consumer;
  private TransactionsDbAdapterConfiguration configuration;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    configuration = new TransactionsDbAdapterConfiguration(config());
  }

  @Override
  public void start() throws Exception {
    LOGGER.info("Starting <{}>", this.getClass().getSimpleName());
    final JDBCClient client = JDBCClient.createShared(vertx, configuration.getClientOptions());

    //register the service proxy on event bus
    consumer = ProxyHelper
        .registerService(AdapterProxy.class, getVertx(),
            new TransactionsDbAdapterProxyImpl(client),
            configuration.getAddress());
  }

  @Override
  public void stop() throws Exception {
    ProxyHelper.unregisterService(consumer);
  }

}
