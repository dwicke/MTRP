package sim.app.mtrp.main.portrayals;

import sim.app.mtrp.main.Task;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;

import java.awt.*;

/**
 * Created by drew on 2/22/17.
 */
public class TaskPortrayal extends OvalPortrayal2D {
    private static final long serialVersionUID = 1;




    @Override
    public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
        super.draw(object, graphics, info);

//        if (!model.getIsAvailable())// then don't draw it
//            graphics.setColor(model.getNotAvailableColor());
//        else
//            graphics.setColor(model.getAvailableColor());

        Task t = (Task) object;


        int alpha = (int)(t.getBounty() / 10);
        if (alpha > 255) {
            alpha = 255;
        }
        graphics.setColor(new Color(0,255,0,alpha));


        // this code was stolen from OvalPortrayal2D
        int x = (int) (info.draw.x - info.draw.width / 2.0);
        int y = (int) (info.draw.y - info.draw.height / 2.0);
        int width = (int) (info.draw.width);
        int height = (int) (info.draw.height);
        graphics.fillOval(x, y, width, height);
    }

}