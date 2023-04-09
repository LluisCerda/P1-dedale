package eu.su.mas.dedaleEtu.mas.agents.dummies.sid;

import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.startMyBehaviours;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.RandomWalkBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SayHelloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.SearchSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;


public class LabAgent extends AbstractDedaleAgent {
    static class HelloWorldBehaviour extends Behaviour {
        public HelloWorldBehaviour(Agent agent) {
            super(agent);
        }

        @Override
        public void action() {
            System.out.println("Lab agent says: \"Hello world!\"");
        }

        @Override
        public boolean done() {
            return true;
        }
    }

    /**
     * This method is automatically called when "agent".start() is executed.
     * Consider that Agent is launched for the first time.
     * 1) set the agent attributes
     * 2) add the behaviours
     */
    protected void setup() {
        super.setup();
        //use them as parameters for your behaviours is you want
        List<Behaviour> lb = new ArrayList<>();

        //Registro de Agentes en el DF
        lb.add(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfd = new DFAgentDescription();
                dfd.setName(getAID());
                ServiceDescription sd = new ServiceDescription();
                sd.setName("LluisExplorer");
                sd.setType("explorer");
                dfd.addServices(sd);
                try {
                    DFService.register(myAgent, dfd);
                } catch (FIPAException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        //Búsqueda de Agentes

        lb.add(new CyclicBehaviour() {
            @Override
            public void action() {

                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription templateSd = new ServiceDescription();
                templateSd.setType("collector");
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
                    LabCollectorAID = dfd.getName();
                    //System.out.println("Found LabCollector: " + provider.toString());
                }

            }
        });

        //Envio de mensages
        lb.add(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(LabCollectorAID);
                msg.setLanguage("English");
                msg.setContent(getCurrentPosition().getLocationId());
                msg.setSender(this.getAgent().getAID());
                sendMessage(msg);
            }
        });

        lb.add(new OneShotBehaviour() {
            @Override
            public void action() {
                ASpawn = getCurrentPosition();
            }
        });

        lb.add(new SearchSoloBehaviour(this, null));

        // MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
        addBehaviour(new startMyBehaviours(this, lb));
    }

    /**
     * This method is automatically called after doDelete()
     */
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

    //AID of the agent we're searching
    AID LabCollectorAID;

    //Spawning location, saved to pass to the other agent
    Location ASpawn;
}
