// Copyright (C) 2010 - 2014 GlavSoft LLC.
// All rights reserved.
//
// -----------------------------------------------------------------------
// This file is part of the TightVNC software.  Please visit our Web site:
//
//                       http://www.tightvnc.com/
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
// -----------------------------------------------------------------------
//
package com.neology.net;

/**
 * @author dime at tightvnc.com
 */
public class BaudrateMeter {

    public static final int MIN_BPS = 10000;
    private static final int n = 5;
    private static final double ALPHA = 2. / (n + 1);
    private double ema = 0;
    private boolean measure = false;
    private long start;
    private long bytes;

    public void count(int bytes) {
        if (measure) this.bytes += bytes;
    }

    public int kBPS() {
        return (int) (ema / 1000);
    }
    
    public int Mbps(){
        return (int)(ema/1000000);
    }

    public void startMeasuringCycle() {
        measure = true;
        start = System.currentTimeMillis();
    }

    public void stopMeasuringCycle() {
        measure = false;
        long ms = System.currentTimeMillis() - start;
        //System.out.println("MS: "+ms);
        if (0 == ms || bytes < 100) return; // skip with too small portion of data
        double bps = bytes * 8. / (ms / 1000.);
//        double bpms = bytes * 8. / ms;
        if (bps < MIN_BPS) { // limit lower value
            bps = MIN_BPS;
        }
        // exponential moving-average smoothing
        ema = ALPHA * bps + (1. - ALPHA) * (0. == ema ? bps : ema);
        bytes = 0;
        
    }
}
