package eu.su.mas.dedaleEtu.mas.agents.dummies.sid;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;

public class LabAgentCollector extends AbstractDedaleAgent {
    protected void setup() {
        super.setup();

        List<Behaviour> lb = new ArrayList<>();

        lb.add(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfd = new DFAgentDescription();
                dfd.setName(getAID());
                ServiceDescription sd = new ServiceDescription();
                sd.setName("LluisCollector");
                sd.setType("collector");
                dfd.addServices(sd);
                try {
                    DFService.register(myAgent, dfd);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        /*
        lb.add(new CyclicBehaviour() {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription templateSd = new ServiceDescription();
                templateSd.setType("explorer");
                template.addServices(templateSd);
                SearchConstraints sc = new SearchConstraints();
                // We want to receive 10 results at most
                sc.setMaxResults(10L);
                DFAgentDescription[] results;
                try {
                    results = DFService.search(this.getAgent(), template, sc);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
                if (results.length > 0) {
                    DFAgentDescription dfd = results[0];
                    AID provider = dfd.getName();
                    System.out.println("Found Lab: " + provider.toString());
                }

            }
        });
        */

        lb.add(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = myAgent.receive();
                if (msg != null) {
                    System.out.println("Message received: " + msg.getContent());
                }
                else {
                    block();
                }
            }
        });

        addBehaviour(new startMyBehaviours(this, lb));
    }

    protected void takeDown() {
        super.takeDown();
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is automatically called before migration.
     * You can add here all the saving you need
     */
    protected void beforeMove() {
        super.beforeMove();
    }

    /**
     * This method is automatically called after migration to reload.
     * You can add here all the info regarding the state you want your agent to restart from
     */
    protected void afterMove() {
        super.afterMove();
    }
}
