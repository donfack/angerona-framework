package angerona.fw.defendingagent.Prover;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.SynchronousQueue;


import se.sics.jasper.Query;
import se.sics.jasper.SICStus;
import se.sics.jasper.SPException;

/**
 * This class lets users to ask the theorem prover to find a closed tree for a
 * set of formulas; public method prove interacts with SICStus Prolog
 * implementation of SeqS using package se.sics.jasper's classes; these
 * implementations consist of files with 'sav' extension
 */
public class Prover {
	
	private static Prover instance = new Prover();
	
	public enum InferenceSystem {
	    CUMMULATIV, LOOP_CUMMULATIV, PREFERENTIAL, 
	    RATIONAL, FREE_RATIONAL
	}
	
	private static SynchronousQueue<ProverInput> inputQueue = new SynchronousQueue<ProverInput>();
	private static SynchronousQueue<Boolean> outputQueue = new SynchronousQueue<Boolean>();
	private static boolean run = true;

	/**
	 * The following object is used to interact with the SICStus Prolog kernel
	 * */
	private static SICStus sp;

	
	
	public static Prover getInstance() {
		return instance;
	}
	
	/**
	 * C´tor
	 */
	private Prover() {
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				if (sp == null) {
					/* Instantiating a SICStus object */
					try {
						sp = new SICStus();
					} catch (SPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				workerThread();
			}
		}).start();
	}
	
	public void stopSICStusThread() {
		run = false;
	}
	
	private void workerThread() {
		while(run) {
			ProverInput input;
				try {
					input = inputQueue.take();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					continue;
				}
				System.out.println("sicstus worker: invoked sicstus worker thread");
			
			boolean result = runProver(input.kFormulas, input.formulaToProve, input.chooseInferenceSystem);
			System.out.println("sicstus worker: prover result " + result);
			try {
				outputQueue.put(result);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}
	
	/**
	 * Read the knowledgebase and the formula to prove and give it the prolog
	 * solver to prove
	 * 
	 * @param kFormulas
	 *            the knowledgebase
	 * @param formulaToProve
	 *            the formula to prove
	 * @param chooseInferenceSystem
	 *            CUMMULATIV - Cumulative logic, 
	 *            LOOP_CUMMULATIV - Loop-Cumulative logic, 
	 *            PREFERENTIAL - Preferential logic, 
	 *            RATIONAL - Rational logic, 
	 *            FREE_RATIONAL - Rational logic with free variables
	 * @return true if formulaToProve can be inferred from kFormulas, false otherwise
	 */
	public boolean prove(List<String> kFormulas, String formulaToProve,
			InferenceSystem chooseInferenceSystem) throws SICStusException {
		
		ProverInput input = new ProverInput(kFormulas, formulaToProve, chooseInferenceSystem);
		try {
			inputQueue.put(input);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			throw new SICStusException();
		}
		
		try {
			System.out.println("waiting for sicstus result");
			return outputQueue.take();
		} catch (InterruptedException e) {
			throw new SICStusException();
		}
	}

	/**
	 * Read the knowledgebase and the formula to prove and give it the prolog
	 * solver to prove
	 * 
	 * @param kFormulas
	 *            the knowledgebase
	 * @param formulaToProve
	 *            the formula to prove
	 * @param chooseInferenceSystem
	 *            CUMMULATIV - Cumulative logic, 
	 *            LOOP_CUMMULATIV - Loop-Cumulative logic, 
	 *            PREFERENTIAL - Preferential logic, 
	 *            RATIONAL - Rational logic, 
	 *            FREE_RATIONAL - Rational logic with free variables
	 * @return true if formulaToProve can be inferred from kFormulas, false otherwise
	 */
	public static boolean runProver(List<String> kFormulas, String formulaToProve,
			InferenceSystem chooseInferenceSystem) {
		
		Query q;
		HashMap<Object, Object> map = new HashMap<Object, Object>();

		/* Initialize the SICStus Prolog engine */
		try {
			/* Parameter 1 determines the KLM logic to consider */
			switch (chooseInferenceSystem) {
			case CUMMULATIV: {
				sp.restore("resources/tct.sav");
				break;
			}
			case LOOP_CUMMULATIV: {
				sp.restore("resources/tclt.sav");
				break;
			}
			case PREFERENTIAL: {
				sp.restore("resources/tpt.sav");
				break;
			}
			case RATIONAL: {
				sp.restore("resources/trt.sav");
				break;
			}
			case FREE_RATIONAL: {
				sp.restore("resources/trtfree.sav");
				break;
			}
			}

			/*
			 * The following statements build the goal to query to the theorem
			 * prover
			 */
			/*
			 * Step 1: formulas of the knowledge base must be in the KLM
			 * language
			 */
			String kBaseList = new String("[");
			for (String currentFormula : kFormulas) {
//				String currentFormula = this.kFormulas[i];
				if (currentFormula.length() > 0)
					kBaseList = kBaseList + currentFormula + ",";
			}
			if (kBaseList.charAt(kBaseList.length() - 1) == ',')
				kBaseList = kBaseList.substring(0, kBaseList.length() - 1);
			kBaseList = kBaseList + "]";

			String goal = new String("parseinput(" + kBaseList + ").");
			q = sp.openPrologQuery(goal, map);
			if (!(q.nextSolution())) {
				System.err
						.println("Error in the knowledge base: you cannot use nested conditionals");
			}

			/* Step 2: formulas to prove base must be in the KLM language */
			String toProveList = new String("[" + formulaToProve + "]");

			goal = new String("parseinput(" + toProveList + ").");
			System.out.println("GOAL: "+goal);
			q = sp.openPrologQuery(goal, map);
			if (!(q.nextSolution())) {
				System.err
						.println("Error in the formula to prove: you cannot use nested conditionals");
			}
			/*
			 * Step 3: finding a derivation of the formula from the knowledge
			 * base by using the calculi for KLM logics
			 */

			goal = new String("unsatinterface(" + kBaseList + "," + toProveList
					+ ",Tree).");
			System.out.println("kbaselist:" + kBaseList);
			q = sp.openPrologQuery(goal, map);
			if (q.nextSolution()) {
				//return new String(map.toString());
				return true;

			} else {
				//return null;
				return false;
			}

		} catch (Exception choosingKLM) {
			
			System.out.println("\nERROR SICStus Prolog engine");
			choosingKLM.printStackTrace();
		}
		return false;

	}
}