/*
 * Copyright 2008-2011 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.torquebox.jobs;

import org.jboss.logging.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.torquebox.core.component.ComponentResolver;
import org.torquebox.core.runtime.RubyRuntimePool;

public class RubyJobProxyFactory implements JobFactory {

    public static final String RUBY_CLASS_NAME_KEY = "torquebox.ruby.class.name";
    public static final String RUBY_REQUIRE_PATH_KEY = "torquebox.ruby.require.path";
    public static final String COMPONENT_RESOLVER_NAME = "torquebox.ruby.component.resolver.name";

    private RubyRuntimePool runtimePool;

    public RubyJobProxyFactory() {
    }

    public void setRubyRuntimePool(RubyRuntimePool runtimePool) {
        this.runtimePool = runtimePool;
    }

    public RubyRuntimePool getRubyRuntimePool() {
        return this.runtimePool;
    }
    

    @Override
    public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
        RubyJobProxy rubyJob = null;

        JobDetail jobDetail = bundle.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        
        log.info( "TOBY: I'd create a job if only I had a component resolver");
//        RubyComponentResolver resolver = getComponentResolver( jobDataMap.getString( RubyJobProxyFactory.COMPONENT_RESOLVER_NAME ) );
//        
//        Ruby ruby = null;
//        try {
//            ruby = this.runtimePool.borrowRuntime();
//            IRubyObject rubyObject = resolver.resolve( ruby );
//            rubyJob = new RubyJobProxy( this.runtimePool, rubyObject );
//        } catch (Exception e) {
//            if (ruby != null) {
//                this.runtimePool.returnRuntime( ruby );
//            }
//            throw new SchedulerException( e );
//        }

        return rubyJob;
    }
    
    // TODO Fix me!
    protected ComponentResolver getComponentResolver(String name) {
        return null;
    }

    private static final Logger log = Logger.getLogger( "org.torquebox.jobs" );
}