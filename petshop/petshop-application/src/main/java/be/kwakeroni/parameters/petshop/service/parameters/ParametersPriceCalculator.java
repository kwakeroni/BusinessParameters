package be.kwakeroni.parameters.petshop.service.parameters;

//import be.kwakeroni.parameters.basic.client.model.Mapped;
//import be.kwakeroni.parameters.basic.client.model.Ranged;
//import be.kwakeroni.parameters.basic.client.model.Simple;
//import be.kwakeroni.parameters.basic.client.query.MappedQuery;
//import be.kwakeroni.parameters.basic.client.query.RangedQuery;
//import be.kwakeroni.parameters.basic.client.query.ValueQuery;
//import be.kwakeroni.parameters.client.api.BusinessParameters;
//import be.kwakeroni.parameters.client.api.model.Parameter;
//import be.kwakeroni.parameters.client.api.model.ParameterGroup;
//import be.kwakeroni.parameters.client.api.query.Query;
//import be.kwakeroni.parameters.petshop.service.AbstractPriceCalculator;
//import be.kwakeroni.parameters.types.support.ParameterTypes;
//
//import java.util.Optional;

/**
 * Created by kwakeroni on 07/11/17.
 */
//class ParametersPriceCalculator extends AbstractPriceCalculator {
//
//    private BusinessParameters businessParameters;
//
//    public ParametersPriceCalculator(BusinessParameters businessParameters) {
//        this.businessParameters = businessParameters;
//    }
//
//
//    protected Optional<Integer> getSalesPercentage(String species, int quantity) {
//        Optional<Integer> pct = getParameter(species, quantity);
//        if (!pct.isPresent()) {
//            pct = getParameter("any", quantity);
//        }
//        return pct;
//    }
//
//    private Optional<Integer> getParameter(String species, int quantity) {
//        return businessParameters.get(SALES, getQuery(species, quantity));
//    }
//
//    private Query<Mapped<String, Ranged<Integer, Simple>>, Integer> getQuery(String species, int quantity) {
//        return new MappedQuery<>(species, ParameterTypes.STRING,
//                new RangedQuery<>(quantity, ParameterTypes.INT,
//                        new ValueQuery<>(PERCENTAGE)
//                )
//        );
//    }
//
//    protected static ParameterGroup<Mapped<String, Ranged<Integer, Simple>>> SALES = new ParameterGroup<Mapped<String, Ranged<Integer, Simple>>>() {
//        @Override
//        public String getName() {
//            return "petshop.sales";
//        }
//    };
//
//    protected static final Parameter<Integer> PERCENTAGE = new Parameter<Integer>() {
//        @Override
//        public String getName() {
//            return "percentage";
//        }
//
//        @Override
//        public Integer fromString(String value) {
//            return ParameterTypes.INT.fromString(value);
//        }
//
//        @Override
//        public String toString(Integer value) {
//            return ParameterTypes.INT.toString(value);
//        }
//    };
//
//}
