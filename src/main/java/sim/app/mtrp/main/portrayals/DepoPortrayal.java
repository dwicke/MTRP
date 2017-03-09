package sim.app.mtrp.main.portrayals;

import sim.app.mtrp.main.Depo;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

import java.awt.*;

/**
 * Created by drew on 2/22/17.
 */
public class DepoPortrayal extends OvalPortrayal2D {



    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);


        graphics.setColor(Color.BLUE);


        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);
    }
}
