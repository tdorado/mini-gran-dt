package back.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Modela el Jugador de futbol de la vida real, el torneo real, con equipos reales
 * es administrador por el administrador
 * Estos juagadores están en un UNICO torneo a la vez*/
public class PhysicalPlayer implements Serializable{

    private final static int MIN_PRICE = 1000;
    private static final long serialVersionUID = 1L;

    private String name;
    private int price;
    private Properties properties;

    public PhysicalPlayer(String name, int price, Properties properties) {
        this.name = name;
        this.price = price;
        this.properties = properties;
    }

    public PhysicalPlayer(String name, Properties properties) {
        this(name,MIN_PRICE,properties);
    }

    public PhysicalPlayer(String name) {
        this(name,new Properties());
    }

    public PhysicalPlayer(String name, int price) {
        this(name,price,new Properties());
    }
    /**
     * @return el nombre del jugador*/
    public String getName() {
        return name;
    }
    /**
     * @return el precio del jugador en el torneo*/
    public int getPrice() { return price; }
    /**
     * @return las propiedades*/
    public Properties getProperties() { return properties; }
    /**
     * @return el los puntos que tiene este juagador en el toreno fisico
     * este puntaje se calcula con un peso ponderado*/
    public int getPoints() {
        return properties.calculateRanking();
    }
    /**
     * Se ocupa de hacer un update en las propiedades de juagador
     * Estas se cambian cuando el adminsitrador del toreno hace un cambio
     * @param p las nuevas propiedades a ser SUMADAS*/
    public void refresh(Properties p) {
        properties.refresh(p);
        price = properties.calculatePrice();
    }

    /**
     * Metodo para el simulador.
     * Carga el precio del jugador
     * */
    public void refreshPrice(){
        this.price = properties.calculatePrice();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhysicalPlayer physicalPlayer = (PhysicalPlayer) o;

        return name != null ? name.equals(physicalPlayer.name) : physicalPlayer.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode()*7 : 0;
    }

    @Override
    public String toString() {
        return name;
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.defaultWriteObject();
        out.writeUTF(name);
        out.writeInt(price);
        out.writeObject(properties);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
        name = ois.readUTF();
        price = ois.readInt();
        properties = (Properties) ois.readObject();
    }

    /**Modela las propiedades del jugador, estos son acumulados a lo largo del torneo.*/
    public static class Properties implements Serializable {

        private int normal_goals_scored;
        private int goals_scored_by_penalty_kick;
        private int penalty_catched;
        private int goals_scored_goalkeeper;
        private int yellow_cards;
        private int red_cards;
        private int goals_against;

        public Properties() {
            this.normal_goals_scored = 0;
            this.goals_scored_by_penalty_kick = 0;
            this.penalty_catched = 0;
            this.goals_scored_goalkeeper = 0;
            this.yellow_cards = 0;
            this.red_cards = 0;
            this.goals_against = 0;
        }

        public Properties(int normal_goals_scored, int goals_scored_by_penalty_kick, int penalty_catched, int goals_scored_goalkeeper, int yellow_cards, int red_cards, int goals_against) {
            this.normal_goals_scored = normal_goals_scored;
            this.goals_scored_by_penalty_kick = goals_scored_by_penalty_kick;
            this.penalty_catched = penalty_catched;
            this.goals_scored_goalkeeper = goals_scored_goalkeeper;
            this.yellow_cards = yellow_cards;
            this.red_cards = red_cards;
            this.goals_against = goals_against;
        }
        /**
         * @return un entero con el puntaje de cada juagor. Este es lineal*/
        public int getPoints() {
            return normal_goals_scored+goals_scored_by_penalty_kick+penalty_catched+goals_scored_goalkeeper-yellow_cards-red_cards-goals_against;
        }
        /**Se setea cada propiedad por separado
         * @param index el indice de la propiedad
         * @param property el valor de la misma*/
        public void setProperty(int index, int property) {
            switch (index) {
                case 0: normal_goals_scored = property;
                        break;
                case 1: goals_scored_by_penalty_kick = property;
                        break;
                case 2: penalty_catched = property;
                        break;
                case 3: goals_scored_goalkeeper = property;
                        break;
                case 4: yellow_cards = property;
                        break;
                case 5: red_cards = property;
                        break;
                case 6: goals_against = property;
                        break;
            }
        }
        /**
         * @param index el indice de la propiedad a retornar
         * @return entero con el valor de la misma*/
        public int getProperty(int index) {
            switch (index) {
                case 0: return normal_goals_scored;
                case 1: return goals_scored_by_penalty_kick;
                case 2: return penalty_catched;
                case 3: return goals_scored_goalkeeper;
                case 4: return yellow_cards;
                case 5: return red_cards;
                case 6: return goals_against;
            }
            return 0;
        }
        /**
         * Se calcula el ranking del jugador de manera ponderada
         * @return entero con el valor*/
        public int calculateRanking() {
            int resp=0;
            resp += normal_goals_scored             * PropValues.normal_goals_scored.getRankingValue();
            resp += goals_scored_by_penalty_kick    * PropValues.goals_scored_by_penalty_kick.getRankingValue();
            resp += penalty_catched                 * PropValues.penalty_catched.getRankingValue();
            resp += goals_scored_goalkeeper         * PropValues.goals_scored_goalkeeper.getRankingValue();
            resp += yellow_cards                    * PropValues.yellow_cards.getRankingValue();
            resp += red_cards                       * PropValues.red_cards.getRankingValue();
            resp += goals_against                   * PropValues.goals_against.getRankingValue();
            return resp;
        }
        /**
         * Se calcula el precio del jugador de manera ponderada
         * @return entero con el valor*/
        public int calculatePrice() {
            int resp = MIN_PRICE;
            resp += normal_goals_scored         * 100 * PropValues.normal_goals_scored.getPricePerCentValue();
            resp += goals_scored_by_penalty_kick* 100 * PropValues.goals_scored_by_penalty_kick.getPricePerCentValue();
            resp += penalty_catched             * 100 * PropValues.penalty_catched.getPricePerCentValue();
            resp += goals_scored_goalkeeper     * 100 * PropValues.goals_scored_goalkeeper.getPricePerCentValue();
            return resp;
        }
        /**
         * Se encarga de cambiar los valores que sean necesarios
         * @param p los nuevos valores*/
        void refresh(Properties p) {
            this.normal_goals_scored            = p.normal_goals_scored;
            this.goals_scored_by_penalty_kick   = p.goals_scored_by_penalty_kick;
            this.penalty_catched                = p.penalty_catched;
            this.goals_scored_goalkeeper        = p.goals_scored_goalkeeper;
            this.yellow_cards                   = p.yellow_cards;
            this.red_cards                      = p.red_cards;
            this.goals_against                  = p.goals_against;
        }

        @Override
        public String toString() {
            return "Properties{" +
                    "normal_goals_scored=" + normal_goals_scored +
                    ", goals_scored_by_penalty_kick=" + goals_scored_by_penalty_kick +
                    ", penalty_catched=" + penalty_catched +
                    ", goals_scored_goalkeeper=" + goals_scored_goalkeeper +
                    ", yellow_cards=" + yellow_cards +
                    ", red_cards=" + red_cards +
                    ", goals_against=" + goals_against +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Properties that = (Properties) o;

            if (normal_goals_scored != that.normal_goals_scored) return false;
            if (goals_scored_by_penalty_kick != that.goals_scored_by_penalty_kick) return false;
            if (penalty_catched != that.penalty_catched) return false;
            if (goals_scored_goalkeeper != that.goals_scored_goalkeeper) return false;
            if (yellow_cards != that.yellow_cards) return false;
            return red_cards == that.red_cards && goals_against == that.goals_against;
        }

        @Override
        public int hashCode() {
            int result = normal_goals_scored;
            result = 31 * result + goals_scored_by_penalty_kick;
            result = 31 * result + penalty_catched;
            result = 31 * result + goals_scored_goalkeeper;
            result = 31 * result + yellow_cards;
            result = 31 * result + red_cards;
            result = 31 * result + goals_against;
            return result;
        }

        public void writeObject(ObjectOutputStream out) throws IOException{
            out.defaultWriteObject();
            out.writeInt(normal_goals_scored);
            out.writeInt(goals_scored_by_penalty_kick);
            out.writeInt(penalty_catched);
            out.writeInt(goals_scored_goalkeeper);
            out.writeInt(yellow_cards);
            out.writeInt(red_cards);
            out.writeInt(goals_against);
        }

        public void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
            ois.defaultReadObject();
            normal_goals_scored          = ois.readInt();
            goals_scored_by_penalty_kick = ois.readInt();
            penalty_catched              = ois.readInt();
            goals_scored_goalkeeper      = ois.readInt();
            yellow_cards                 = ois.readInt();
            red_cards                    = ois.readInt();
            goals_against                = ois.readInt();
        }
        /**
         * Encargado de contener los valores de cada propiedad*/
        public enum PropValues{
            normal_goals_scored(20,0.35),
            goals_scored_by_penalty_kick(10,0.1),
            penalty_catched(20,0.2),
            goals_scored_goalkeeper(60,0.35),
            yellow_cards(-5,0),
            red_cards(-10,0),
            goals_against(-20,0);

            int pValue;
            double uPricePerCent;

            PropValues(int rValue, double PricePerCent) {
                this.pValue = rValue;
                this.uPricePerCent = PricePerCent;
            }
            /**Es el valor asociado por 1(una) propiedad.
             * Ej 1 gol normal = 20 puntos */
            public int getRankingValue() {
                return pValue;
            }
            /**Es el porcentaje dle precio asociado por 1(una) propiedad.
             * Ej 1 gol normal = 0.35 de su precio */
            public double getPricePerCentValue() {
                return uPricePerCent;
            }
        }
    }
}
