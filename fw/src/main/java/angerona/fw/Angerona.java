package angerona.fw;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import angerona.fw.serialize.AgentConfiguration;
import angerona.fw.serialize.BeliefbaseConfiguration;
import angerona.fw.serialize.SimulationConfiguration;

/**
 * Main class of Angerona manages all resources.
 * Give the user the ability to add new folders
 * as resource folders, all the files in those folders
 * will be loaded.
 * @author Tim Janus
 * TODO: Also handle plugins as resource.
 *
 */
public class Angerona {
	
	private static Logger LOG = LoggerFactory.getLogger(Angerona.class);
	
	/**
	 * Handle different resource file loadings by fileformat.
	 * @author Tim Janus
	 */
	private interface FileLoader {
		void load(File file, Angerona container) throws ParserConfigurationException, SAXException, IOException;
	}
	
	/**
	 * Implementation for the Agent-Configuration file format.
	 * @author Tim Janus
	 */
	private class AgentConfigLoader implements FileLoader {
		@Override
		public void load(File file, Angerona container) throws ParserConfigurationException, SAXException, IOException {
			List<AgentConfiguration> acs = AgentConfiguration.loadXml(file.getAbsolutePath());
			for(AgentConfiguration ac : acs) {
				container.agentConfigurations.put(ac.getName(), ac);
			}
		}
	}
	
	/**
	 * Implementation for the Belief-Base-configuration file format.
	 * @author Tim Janus
	 */
	private class BeliefbaseConfigLoader implements FileLoader {
		@Override
		public void load(File file, Angerona container) throws ParserConfigurationException, SAXException, IOException {
			List<BeliefbaseConfiguration> bbs = BeliefbaseConfiguration.loadXml(file.getAbsolutePath());
			for(BeliefbaseConfiguration bbc : bbs) {
				container.beliefbaseConfigurations.put(bbc.getName(), bbc);
			}
		}
	}
	
	/**
	 * Implementation for the Simulation-Configuration file format.
	 * @author Tim Janus
	 *
	 */
	private class SimulationConfigLoader implements FileLoader {
		@Override
		public void load(File file, Angerona container) throws ParserConfigurationException, SAXException, IOException {
			List<SimulationConfiguration> scs = SimulationConfiguration.loadXml(file.getAbsolutePath());
			for(SimulationConfiguration sc : scs) {
				container.simulationConfigurations.put(sc.getName(), sc);
			}
		}
	}
	
	/** flag indicating if the bootstrap process is already done. */
	private boolean bootstrapDone = false;
	
	/** set containing all folders where agent configuration files are stored. */
	private Set<String> agentConfigFolders = new HashSet<String>();
	
	/** set containing all folders where belief base configuration files are stored. */
	private Set<String> bbConfigFolders = new HashSet<String>();
	
	/** set containing all folders where simulation configuration files are stored. */
	private Set<String> simulationFolders = new HashSet<String>();
	
	/** map containing all loaded Agent Configurations ordered by name */
	protected Map<String, AgentConfiguration> agentConfigurations = new HashMap<String, AgentConfiguration>();
	
	/** map containing all loaded Beliefbase Configurations ordered by name */
	Map<String, BeliefbaseConfiguration> beliefbaseConfigurations = new HashMap<String, BeliefbaseConfiguration>();
	
	/** map containing all loaded Simulation Configurations ordered by name */
	Map<String, SimulationConfiguration> simulationConfigurations = new HashMap<String, SimulationConfiguration>();
	
	/**
	 * Adds the given folder to the set of AgentConfiguration folders, if bootstrap is already done
	 * the loading start immediatley otherwise the files in the folder will be load after a call of 
	 * bootstrap
	 * @param folder	name of the folder containing AgentConfiguration files.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void addAgentConfigFolder(String folder) throws ParserConfigurationException, SAXException, IOException {
		if(agentConfigFolders.add(folder) && bootstrapDone) {
			forAllFilesIn(folder, new AgentConfigLoader());
		}
	}
	
	/**
	 * Adds the given folder to the set of BeliefbaseConfiguration folders, if bootstrap is already done
	 * the loading start immediately otherwise the files in the folder will be load after a call of 
	 * bootstrap
	 * @param folder	name of the folder containing BeliefbaseConfiguration files.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void addBeliefbaseConfigFolder(String folder) throws IOException, ParserConfigurationException, SAXException {
		if(bbConfigFolders.add(folder) && bootstrapDone) {
			forAllFilesIn(folder, new BeliefbaseConfigLoader());
		}
	}
	
	/**
	 * Adds the given folder to the set of Simulation folders, if bootstrap is already done
	 * the loading start immediately otherwise the files in the folder will be load after a call of 
	 * bootstrap
	 * @param folder	name of the folder containing Simulation files.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void addSimulationFolders(String folder) throws IOException, ParserConfigurationException, SAXException {
		if(simulationFolders.add(folder) && bootstrapDone) {
			forAllFilesIn(folder, new SimulationConfigLoader());
		}
	}
	
	/**
	 * Loads the resources in the folders registered so far. First of all the
	 * AgentConfigurations are loaded then the Beliefbase Configurations and
	 * after that the Simulation templates.
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public void bootstrap() throws IOException, ParserConfigurationException, SAXException {
		if(!bootstrapDone) {
			AgentConfigLoader acl = new AgentConfigLoader();
			for(String folder : agentConfigFolders) {
				forAllFilesIn(folder, acl);
			}
			
			BeliefbaseConfigLoader bbcl = new BeliefbaseConfigLoader();
			for(String folder : bbConfigFolders) {
				forAllFilesIn(folder, bbcl);
			}
			
			SimulationConfigLoader scl = new SimulationConfigLoader();
			for(String folder : simulationFolders) {
				forAllFilesIn(folder, scl);
			}
		}
		bootstrapDone = true;
	}
	
	/** @return true if a bootstrap method was called otherwise false. */
	public boolean isBootstrapDone() {
		return bootstrapDone;
	}
	
	/**
	 * Helper method: Recursively tries to load all files in the directory and in
	 * its sub-directories using the given FileLoader implementation.
	 * @param folder	root folder to scan for files to load.
	 * @param loader	Implementation of the used loader.
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	private void forAllFilesIn(String folder, FileLoader loader) throws IOException, ParserConfigurationException, SAXException {
		File dir = new File(folder);
		if(!dir.isDirectory())
			throw new IOException(folder + " is no directory.");
		
		File [] files = dir.listFiles();
		if(files == null)
			return;
		for(File actFile : files) {
			try {
				if(actFile.isFile())
					loader.load(actFile, this);
				else if(actFile.isDirectory())
					forAllFilesIn(actFile.getAbsolutePath(), loader);
			} catch(Exception ex) {
				LOG.warn("Cannot load file: '"+actFile.getName()+"' " + ex.getMessage());
			} 
		}
	}
}