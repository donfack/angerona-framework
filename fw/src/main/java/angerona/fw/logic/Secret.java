package angerona.fw.logic;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;

import net.sf.tweety.Formula;
import net.sf.tweety.logics.firstorderlogic.syntax.FolFormula;
import angerona.fw.util.Pair;

/**
 * A secret as defined Def. 4 in "Agent-based Epistemic Secrecy" 
 * of Krümpelmann and Kern-Isberner.
 * The belief/reasoning-operator is not dynamically yet, but the
 * reasoning operator linked to the used knowledge base is used.
 * 
 * For example:
 * We have the agents Alice, Bob and Claire. And Bob does not want
 * his wife Alice to know that he has an affair with Claire. Also
 * he does not want Claire to know that he has children.
 * 
 * @remark
 * In Angerona Bobs data has the Beliefbases B_A and B_C
 * representing the view on Alice or Claire. The reasoning operators
 * linked to these beliefbases are used to instead one defined in
 * this data-strcuture.
 * TODO: Change this
 * 
 * @author Tim Janus
 */
public class Secret implements Cloneable {
	public static final String DEFAULT_BELIEFCHANGE = "__DEFAULT__";
	
	/** name of the agent who should not get the information */
	private String name;
	
	/** formula representing the confidential information */
	private FolFormula information;
	
	/** the java class name which should be used to proof this secret */
	private String reasonerClass;
	
	/** a map containing parameters for the used reasoner class */
	private Map<String, String> reasonerParameters;
	
	private List<PropertyChangeListener> listeners = new LinkedList<PropertyChangeListener>();
	
	public boolean addPropertyListener(PropertyChangeListener listener) {
		return listeners.add(listener);
	}
	
	public boolean removePropertyListener(PropertyChangeListener listener) {
		return listeners.remove(listener);
	}
	
	protected void invokePropertyListener(String propertyName, Object oldValue, Object newValue) {
		for(PropertyChangeListener l : listeners) {
			l.propertyChange(new PropertyChangeEvent(this, propertyName, oldValue, newValue));
		}
	}
	
	public Secret(Secret other) {
		name = other.name;
		information = other.information;
		reasonerClass = other.reasonerClass;
		reasonerParameters = new HashMap<String, String>(other.reasonerParameters);
	}
	
	public Secret(String name, FolFormula information, String reasonerClass) {
		this(name, information, reasonerClass, new HashMap<String, String>());
	}
	
	public Secret(String name, FolFormula information,
			String reasonerClass, Map<String, String> parameters) {
		this.name = name;
		this.information = information;
		this.reasonerClass = reasonerClass;
		this.reasonerParameters = parameters;
	}
	
	/**	@return name of the agent who should not get the information */
	public String getSubjectName() {
		return name;
	}
	
	/** @return formula representing the confidential information */
	public Formula getInformation() {
		return information;
	}
	
	/** 
	 * @return 	the full java class name which should be used for revision 
	 *			might be DEFAULT_CHANGEBELIEF with the meaning the default
	 *			change operator of the beliefbase should be used.
	 */
	public String getReasonerClassName() {
		return reasonerClass;
	}
	
	public Map<String, String> getReasonerParameters() {
		return reasonerParameters;
	}
	
	public Pair<String, Map<String, String>> getPair() {
		return new Pair<String, Map<String, String>>(reasonerClass, reasonerParameters);
	}
	
	public void setReasonerParameters(Map<String, String> parameters) {
		invokePropertyListener("reasonerParameters", reasonerParameters, parameters);
		reasonerParameters = parameters;
	}
	
	@Override
	public Object clone() {
		return new Secret(this);
	}
	
	@Override
	public String toString() {
		return "(" + name + "," + information +  "," + reasonerClass + "(" + reasonerParameters +  "))";
	}
}