package com.viveret.pilexa.pi.skill;

import com.viveret.pilexa.pi.ConcretePiLexaService;
import com.viveret.pilexa.pi.PiLexaService;
import com.viveret.pilexa.pi.invocation.Invocation;
import com.viveret.pilexa.pi.invocation.InvocationPattern;
import com.viveret.pilexa.pi.invocation.InvocationProcessor;
import com.viveret.pilexa.pi.invocation.PooledInvocationPattern;
import com.viveret.pilexa.pi.sayable.Sayable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by viveret on 2/5/17.
 */
public class ManifestIntent implements Intent {
    private final List<InvocationPattern> myPatterns = new ArrayList<>();

    private Skill myParentSkill;
    private String myName, myDesc;
    private InvocationProcessor myInvocationProcessor;

    public ManifestIntent(JSONObject root, Skill parent) throws ClassNotFoundException {
        myParentSkill = parent;

        String className = parent.getPackageName() + "." + root.get("className");

        Class<?> clazz = Class.forName(className);
        Constructor<?> ctor;
        try {
            ctor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new ClassNotFoundException(e.toString());
        }

        try {
            myInvocationProcessor = (InvocationProcessor) ctor.newInstance();
        } catch (InstantiationException e) {
            throw new ClassNotFoundException(e.toString());
        } catch (IllegalAccessException e) {
            throw new ClassNotFoundException(e.toString());
        } catch (InvocationTargetException e) {
            throw new ClassNotFoundException(e.toString());
        }

        myName = root.getString("name");
        myDesc = root.getString("desc");

        JSONArray invocations = root.getJSONArray("invocations");
        for (int i = 0; i < invocations.length(); i++) {
            String patt = (String) invocations.get(i);
            myPatterns.add(new PooledInvocationPattern(patt));
        }
    }

    public PiLexaService getConnectedPilexaService() {
        return ConcretePiLexaService.inst();
    }

    @Override
    public Skill getAssociatedSkill() {
        return myParentSkill;
    }

    @Override
    public String getDisplayName() {
        return myName;
    }

    @Override
    public String getShortDescription() {
        return myDesc;
    }

    @Override
    public String getDescription() {
        return myDesc;
    }

    @Override
    public List<InvocationPattern> getInvocationPatterns() {
        return myPatterns;
    }

    @Override
    public Sayable processInvocation(Invocation i) {
        return myInvocationProcessor.processInvocation(i);
    }
}
