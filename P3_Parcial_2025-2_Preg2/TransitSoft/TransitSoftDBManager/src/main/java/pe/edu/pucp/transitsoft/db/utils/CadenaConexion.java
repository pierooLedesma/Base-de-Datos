package pe.edu.pucp.transitsoft.db.utils;

public class CadenaConexion {
    private String servidor;
    private String schema;
    private int puerto;
    private String tipoDB;

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getTipoDB() {
        return tipoDB;
    }

    public void setTipoDB(String tipoDB) {
        this.tipoDB = tipoDB;
    }

    CadenaConexion(Builder builder) {
        this.servidor = builder.getServidor();
        this.schema = builder.getSchema();
        this.puerto = builder.getPuerto();
        this.tipoDB = builder.getTipoDB();
        this.tipoDB = builder.getTipoDB();
    }

    @Override
    public String toString() {
        if (this.tipoDB.equals("MySQL")) {
            return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&"
                    + "allowPublicKeyRetrieval=true", servidor, puerto, schema);
        } else {
            throw new UnsupportedOperationException("Tipo DB no " + "soportado: " + tipoDB);
        }
    }

    public static class Builder {
        private String servidor;
        private String schema;
        private int puerto;
        private String tipoDB;

        public String getServidor() {
            return this.servidor;
        }

        public String getSchema() {
            return this.schema;
        }

        public int getPuerto() {
            return this.puerto;
        }

        public String getTipoDB() {
            return this.tipoDB;
        }

        public Builder servidor(String servidor) {
            this.servidor = servidor;
            return this;
        }

        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        public Builder puerto(int puerto) {
            this.puerto = puerto;
            return this;
        }

        public Builder tipoDB(String tipoDB) {
            this.tipoDB = tipoDB;
            return this;
        }

        public CadenaConexion build() {
            return new CadenaConexion(this);
        }
    }
}
