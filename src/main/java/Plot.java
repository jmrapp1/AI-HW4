import jahuwaldt.plot.*;

import javax.swing.*;
import java.awt.*;

public class Plot {

    public Plot(int generationCount, double[] values) {
        double[] xVals = new double[generationCount];
        for (int i = 0; i < generationCount; i++) xVals[i] = i + 1;
        Plot2D aPlot = new SimplePlotXY(xVals, values, "Static Deflection", "Generation No.", "Avg.Population Fitness", null, null, null);
// Make the horizontal axis a log axis.
                PlotAxis xAxis = aPlot.getHorizontalAxis();
        xAxis.setScale(new LinearAxisScale());
        PlotPanel panel = new PlotPanel(aPlot);
        panel.setBackground(Color.white);
        PlotWindow window = new PlotWindow("SimplePlotXY Plot Window", panel);
        window.setSize(500, 300);
        window.setLocation(250, 250); // location on screen
        window.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        window.show();
    }

}
