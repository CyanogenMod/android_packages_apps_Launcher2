/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adw.launcher;

import android.view.animation.Interpolator;

/**
 * An interpolator where the rate of change starts out quickly and 
 * and then decelerates.
 *
 */
public class ElasticInterpolator implements Interpolator {
	private final float mTension;
	/**
     * Constructor
     * 
     * @param factor Degree to which the animation should be eased. Seting factor to 1.0f produces
     *        an upside-down y=x^2 parabola. Increasing factor above 1.0f makes exaggerates the
     *        ease-out effect (i.e., it starts even faster and ends evens slower)
     */
    public ElasticInterpolator(float tension) {
        mTension = tension;
    }
       
    public float getInterpolation(float t) {
        // _o(t) = t * t * ((tension + 1) * t + tension)
        // o(t) = _o(t - 1) + 1
        t -= 1.0f;
        return t * t * t * t * ((mTension + 1) * t + mTension) + 1.0f;
    }
}
