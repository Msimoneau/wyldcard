package com.defano.hypertalk.ast.expressions;

import com.defano.hypertalk.ast.model.Owner;
import com.defano.hypertalk.ast.model.PartType;
import com.defano.hypertalk.ast.expressions.containers.PartExp;
import com.defano.hypertalk.ast.model.specifiers.PartIdSpecifier;
import com.defano.hypertalk.ast.model.specifiers.PartSpecifier;
import com.defano.hypertalk.exception.HtException;
import org.antlr.v4.runtime.ParserRuleContext;

public class PartIdExp extends PartExp {

    public final Owner layer;
    public final PartType type;
    public final Expression id;

    public PartIdExp(ParserRuleContext context, PartType type, Expression id) {
        this(context, null, type, id);
    }

    public PartIdExp(ParserRuleContext context, Owner layer, PartType type, Expression id) {
        super(context);
        this.layer = layer;
        this.type = type;
        this.id = id;
    }
    
    public PartSpecifier evaluateAsSpecifier () throws HtException
    {        
        return new PartIdSpecifier(layer, type, id.evaluate().integerValue());
    }
    
}
