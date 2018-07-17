/**
 * 
 */
package com.tc.model;

import com.jfinal.plugin.activerecord.Model;

/**
 * @author PB
 *
 */
public class Version extends Model<Version>
{

	public static Version dao = new Version().dao();
}
