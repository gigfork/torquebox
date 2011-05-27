package org.torquebox.core.component;

import static org.junit.Assert.*;

import java.util.Map;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.torquebox.core.runtime.RubyRuntimeFactory;

public class ComponentResolverTest {
    
    private RubyRuntimeFactory factory;
    private Ruby ruby;

    @Before
    public void setUpRuby() throws Exception {
        this.factory = new RubyRuntimeFactory();
        this.ruby = this.factory.createInstance( getClass().getSimpleName() );
    }
    
    @After
    public void tearDownRuby() throws Exception {
        this.factory.destroyInstance(  this.ruby  );
        this.ruby = null;
    }

    /** Ensure that resolution instantiates if-required. */
    @Test
    public void testResolveToInstantiate() throws Exception {
        ComponentResolver resolver = new ComponentResolver(false);
        resolver.setComponentName( "component-foo" );

        this.ruby.evalScriptlet( "class ComponentClass; end" );
        
        ComponentClass componentClass = new ComponentClass();
        componentClass.setClassName( "ComponentClass"  );
        resolver.setComponentInstantiator( componentClass );

        AbstractRubyComponent component = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( component );
        assertNotNull( component.getRubyComponent() );
        
        assertEquals( "ComponentClass", component.getRubyComponent().getMetaClass().getName() );
    }

    /** Ensure that repeated resolutions resolve to the same object. */
    @Test
    public void testResolveRepeatedly() throws Exception {
        ComponentResolver resolver = new ComponentResolver(false);

        this.ruby.evalScriptlet( "class ComponentClass; end" );
        resolver.setComponentName( "component-foo" );
        
        ComponentClass componentClass = new ComponentClass();
        componentClass.setClassName( "ComponentClass"  );
        resolver.setComponentInstantiator( componentClass );

        AbstractRubyComponent component = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( component );
        assertNotNull( component.getRubyComponent() );
        
        assertEquals( "ComponentClass", component.getRubyComponent().getMetaClass().getName() );

        AbstractRubyComponent componentToo = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( componentToo );
        assertNotNull( componentToo.getRubyComponent() );

        // TODO Should the wrapper be identical?
        //assertSame( component, componentToo );
        assertSame( component.getRubyComponent(), componentToo.getRubyComponent() );
    }

    /** Ensure that appropriate file is required/loaded if provided. */
    @Test
    public void testResolveWithRequirePath() throws Exception {
        ComponentResolver resolver = new ComponentResolver(false);
        resolver.setComponentName( "some-component" );
        
        
        ComponentClass componentClass = new ComponentClass();
        componentClass.setClassName( "SomeComponent"  );
        componentClass.setRequirePath( "org/torquebox/core/component/some_component" );
        resolver.setComponentInstantiator( componentClass );
        
        AbstractRubyComponent component = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( component );
        assertNotNull( component.getRubyComponent() );
        
        assertEquals( "SomeComponent", component.getRubyComponent().getMetaClass().getName() );

        AbstractRubyComponent componentToo = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( componentToo );
        assertNotNull( componentToo.getRubyComponent() );

        //assertSame( component, componentToo );
        assertSame( component.getRubyComponent(), componentToo.getRubyComponent() );
    }

    /** Ensure that constructors may take arguments. */
    @SuppressWarnings("rawtypes")
    @Test
    public void testResolveWithContructorArguments() throws Exception {
        ComponentResolver resolver = new ComponentResolver(false);
        
        
        resolver.setComponentName( "optional-component" );
        
        ComponentClass componentClass = new ComponentClass();
        componentClass.setClassName( "OptionalComponent"  );
        componentClass.setRequirePath( "org/torquebox/core/component/optional_component" );
        resolver.setComponentInstantiator( componentClass );
        
        Map options = ruby.evalScriptlet( "{ :a => '1', 'b' => '2' }" ).convertToHash();
        resolver.setInitializeParams( options );
        
        AbstractRubyComponent component = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( component );
        assertNotNull( component.getRubyComponent() );

        assertEquals( "1", JavaEmbedUtils.invokeMethod( this.ruby, component.getRubyComponent(), "[]", new Object[] { ruby.evalScriptlet( ":a" ) }, String.class ) );
        assertEquals( "2", JavaEmbedUtils.invokeMethod( this.ruby, component.getRubyComponent(), "[]", new Object[] { "b" }, String.class ) );
    }

    /**
     * Ensure that multiple resolvers keep their components distinct within an
     * interpreter.
     */
    @Test
    public void testResolveMultipleNames() throws Exception {
        ComponentResolver resolverOne = new ComponentResolver( false );
        resolverOne.setComponentName( "component-one" );
        ComponentClass componentClassOne = new ComponentClass();
        componentClassOne.setClassName( "ComponentClassOne" );
        resolverOne.setComponentInstantiator( componentClassOne );

        ComponentResolver resolverTwo = new ComponentResolver( false );
        resolverTwo.setComponentName( "component-two" );
        ComponentClass componentClassTwo = new ComponentClass();
        componentClassTwo.setClassName( "ComponentClassTwo" );
        resolverTwo.setComponentInstantiator( componentClassTwo );

        this.ruby.evalScriptlet( "class ComponentClassOne; end" );
        this.ruby.evalScriptlet( "class ComponentClassTwo; end" );

        AbstractRubyComponent componentOne = (AbstractRubyComponent) resolverOne.resolve( this.ruby );
        assertNotNull( componentOne );
        assertNotNull( componentOne.getRubyComponent() );
        assertEquals( "ComponentClassOne", componentOne.getRubyComponent().getMetaClass().getName() );

        AbstractRubyComponent componentTwo = (AbstractRubyComponent) resolverTwo.resolve( this.ruby );
        assertNotNull( componentTwo );
        assertNotNull( componentTwo.getRubyComponent() );
        assertEquals( "ComponentClassTwo", componentTwo.getRubyComponent().getMetaClass().getName() );
    }

    /**
     * Ensure that repeated resolutions resolve to different objects when always
     * reloading.
     */
    @Test
    public void testAlwaysReload() throws Exception {
        ComponentResolver resolver = new ComponentResolver( true );
        resolver.setAlwaysReload( true );

        this.ruby.evalScriptlet( "class ComponentClass; end" );
        resolver.setComponentName( "component-foo" );
        
        ComponentClass componentClass = new ComponentClass();
        componentClass.setClassName( "ComponentClass"  );
        resolver.setComponentInstantiator( componentClass );

        AbstractRubyComponent component = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( component );
        assertNotNull( component.getRubyComponent() );
        assertEquals( "ComponentClass", component.getRubyComponent().getMetaClass().getName() );

        AbstractRubyComponent componentToo = (AbstractRubyComponent) resolver.resolve( this.ruby );
        assertNotNull( componentToo );
        assertNotNull( componentToo.getRubyComponent() );
        assertEquals( "ComponentClass", componentToo.getRubyComponent().getMetaClass().getName() );

        assertNotSame( component, componentToo );
        assertNotSame( component.getRubyComponent(), componentToo.getRubyComponent() );
    }

}
