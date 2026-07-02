package com.afs.integratedMachine.recipe.utils.inputEntry;

import com.afs.integratedMachine.recipe.utils.randomSet.RandomSetHandle;
import com.afs.integratedMachine.utils.SimpleSerializer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentPredicateInput extends AbstractInputEntry implements InputEntryEffect.Environment{
    private final List<PredicateStatement> predicateStatements;

    public EnvironmentPredicateInput(List<PredicateStatement> predicateStatements) {
        this.predicateStatements = predicateStatements;
    }

    public List<PredicateStatement> getPredicateStatements() {
        return predicateStatements;
    }

    @Override
    public InputType getEntryType() {
        return InputType.ENVIRONMENT;
    }

    public static final MapCodec<EnvironmentPredicateInput> CODEC = PredicateStatement.CODEC.listOf()
            .xmap(EnvironmentPredicateInput::new, EnvironmentPredicateInput::getPredicateStatements)
            .fieldOf("predicates");

    public static final StreamCodec<ByteBuf, EnvironmentPredicateInput> STREAM_CODEC =
            PredicateStatement.STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(EnvironmentPredicateInput::new, EnvironmentPredicateInput::getPredicateStatements);

    public static final SimpleSerializer<EnvironmentPredicateInput> SERIALIZER = new SimpleSerializer<>(CODEC, STREAM_CODEC);

    @Override
    public SimpleSerializer<? extends AbstractInputEntry> type() {
        return AbstractInputEntry.ENVIRONMENT_PREDICATE.get();
    }

    @Override
    public boolean test(Map<String, Integer> config, RandomSetHandle randomSets) {
        for(var statement:predicateStatements){
            if(!statement.test(config)){
                return false;
            }
        }
        return true;
    }

    public record PredicateStatement(String key, Operator operator, int value){
        @Override
        public @NotNull String toString(){
            return key + operator.getSymbol() + value;
        }

        private static final Pattern paresPattern = Pattern.compile("^([a-zA-Z_][a-zA-Z0-9_]*)(==|!=|>=|<=|>|<)(-?\\d+)$");

        public static PredicateStatement parse(String statement){
            if(statement == null){
                throw new IllegalArgumentException("statement can not be null!");
            }
            Matcher matcher = paresPattern.matcher(statement.trim());
            if(matcher.find()){
                return new PredicateStatement(matcher.group(1), Operator.parse(matcher.group(2)), Integer.parseInt(matcher.group(3)));
            }
            throw new IllegalArgumentException("Cannot parse statement: " + statement);
        }

        public boolean test(Map<String, Integer> config){
            if(!config.containsKey(key)) return false;
            return switch (operator){
                case EQ -> config.get(key) == value;
                case NEQ -> config.get(key) != value;
                case LT -> config.get(key) < value;
                case GT -> config.get(key) > value;
                case LEQ -> config.get(key) <= value;
                case GEQ -> config.get(key) >= value;
            };
        }

        public static final Codec<PredicateStatement> CODEC = Codec.stringResolver(PredicateStatement::toString, PredicateStatement::parse);
        public static final StreamCodec<ByteBuf, PredicateStatement> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(
                 PredicateStatement::parse, PredicateStatement::toString
        );
    }

    public enum Operator{
        EQ("=="), NEQ("!="), LT("<"), GT(">"), LEQ("<="), GEQ(">=");
        Operator(String symbol){
            this.symbol = symbol;
        }
        private final String symbol;
        public String getSymbol() {
            return symbol;
        }
        public static Operator parse(String id){
            Operator[] operators = Operator.values();
            for(Operator op: operators){
                if(op.symbol.equals(id)){
                    return op;
                }
            }
            throw new IllegalStateException(id + "is not a legal operator symbol");
        }
    }
}
