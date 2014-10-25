/**
 * Project: Raspberry Pi Data Hub
 * Date: 10/25/14
 */

package edu.msoe.smv.raspirouter;

/**
 * @author austin
 * @version 0.0001
 */

import android.app.Activity;
import android.os.Bundle;

public class HeadsUpDisplay extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.headsupdisplay);
    }
}