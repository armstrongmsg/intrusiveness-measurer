package commons.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.edu.ufcg.lsd.beefs.commons.config.BasicConfiguration;
import br.edu.ufcg.lsd.beefs.commons.config.Configuration;
import br.edu.ufcg.lsd.beefs.commons.persistence.jdbm.JDBMMapFactory;

import commons.MapFactory;

public class PersistentMapFactory implements MapFactory {

	private JDBMMapFactory mapFactory;
	private static final Logger logger = LoggerFactory.getLogger(PersistentMapFactory.class);
	
	public PersistentMapFactory() {
		mapFactory = new JDBMMapFactory();
	}
	
	@Override
	public void configure(Properties properties) throws IOException {
		logger.debug("Configuration:{}", properties);
		mapFactory.initialize(new BasicConfiguration<Configuration>().configure(properties));
	}

	@Override
	public <K extends Serializable, V extends Serializable> Map<K, V>
									createMap(String name) throws IOException {
		logger.debug("Creating map:{}", name);
		return mapFactory.createMap(name);
	}

	@Override
	public void close() throws IOException {
		logger.debug("Closing factory");
		mapFactory.close();
	}
}
