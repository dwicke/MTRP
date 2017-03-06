package sim.app.mtrp.main.portrayals;

import sim.app.mtrp.main.Agent;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

import java.awt.*;

/**
 * Created by drew on 2/22/17.
 */
public class AgentPortrayal extends OvalPortrayal2D {
    private static final long serialVersionUID = 1;

    Agent model;

    public AgentPortrayal(Agent model) {
        this.model = model;
    }

    @Override
    public final void draw(Object object, Graphics2D graphics, DrawInfo2D info) {

        if (model.isAmWorking()) {
            graphics.setColor(Color.orange);
        } else {
            graphics.setColor(Color.black);
        }
        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);

    }
}
