package uk.ac.ncl.prov.lib.export;

import org.neo4j.graphdb.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by hugofirth on 04/05/2014.
 */
public class ProvnExport implements Export {

    private Path dest;
    private Path file;
    private List<String> nodes;
    private List<String> relationships;

    /**
     * Constructor
     *
     * @param dest The filesystem path of the exported file.
     */
    public ProvnExport(String dest) throws IOException
    {
        this.dest = Paths.get(dest);
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        this.file = Files.createFile(this.dest, attr);
        this.nodes  = new LinkedList<>();
        this.relationships = new LinkedList<>();
    }

    @Override
    public void setFileDestination(String dest) throws IOException
    {
        this.dest = Paths.get(dest).toRealPath();
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-rw-rw-");
        FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
        this.file = Files.createFile(this.dest, attr);
    }

    @Override
    public void export() throws IOException
    {
        try(BufferedWriter writer = Files.newBufferedWriter(this.file, StandardCharsets.UTF_8, StandardOpenOption.APPEND))
        {
            writer.write("document"+System.getProperty("line.separator"));
            for(String n : this.nodes)
            {
                writer.write("    " + n + System.getProperty("line.separator"));
            }
            writer.write(System.getProperty("line.separator"));
            for(String r : this.relationships)
            {
                writer.write("    "+r+System.getProperty("line.separator"));
            }
            writer.write("endDocument"+System.getProperty("line.separator"));
        }
    }

    @Override
    public void serializeNode(Node n)
    {
        StringBuilder output = new StringBuilder();
        String operator = "";
        boolean collection = false;
        boolean includeProperties = true;
        for(Label l : n.getLabels())
        {
            switch (l.name().toLowerCase())
            {
                case "activity" :
                    operator = "activity";
                    break;
                case "agent" :
                    operator = "agent";
                    break;
                case "entity" :
                    operator = "entity";
                    collection = n.hasRelationship(DynamicRelationshipType.withName("HADMEMBER"), Direction.OUTGOING);
                    break;
                default:
                    throw new IllegalArgumentException(l.name()+" is not a recognised PROV-N type.");
            }
        }
        output.append(operator).append("(").append(n.getId());
        if(includeProperties)
        {

            output.append(", [");
            int i = 0;
            for(String key : n.getPropertyKeys())
            {
                if(!key.startsWith("__"))
                {
                    output.append(key).append("=\"").append(n.getProperty(key)).append("\", ");
                    i++;
                }
            }
            if(collection)
            {
                output.append("prov:type=\"prov:Collection\", ");
            }
            if(i>0 || collection)
            {
                output.delete(output.length()-2, output.length()-1);
            }
            output.append("]");
        }
        output.append(")");
        this.nodes.add(output.toString());
    }

    @Override
    public void serializeRelationship(Relationship r) {
        StringBuilder output = new StringBuilder();
        String operator;
        String relType = r.getType().name();
        boolean includeId = true;
        boolean includeProperties = true;
        switch (relType)
        {
            case "WASDERIVEDFROM" :
                operator = "wasDerivedFrom";
                break;
            case "WASGENERATEDBY" :
                operator = "wasGeneratedBy";
                break;
            case "USED" :
                operator = "used";
                break;
            case "WASASSOCIATEDWITH" :
                operator = "wasAssociatedWith";
                break;
            case "HADMEMBER" :
                operator = "hadMember";
                includeId = false;
                break;
            case "WASINVALIDATEDBY" :
                operator = "wasInvalidatedBy";
                break;
            case "WASINFORMEDBY" :
                operator = "wasInformedBy";
                break;
            case "WASATTRIBUTEDTO" :
                operator = "wasAttributedTo";
                break;
            case "ACTEDONBEHALFOF" :
                operator = "actedOnBehalfOf";
                break;
            case "ALTERNATEOF" :
                operator = "alternateOf";
                includeId = false;
                includeProperties = false;
                break;
            case "SPECIALIZATIONOF" :
                operator = "alternateOf";
                includeId = false;
                includeProperties = false;
                break;
            case "WASSTARTEDBY" :
                operator = "wasStartedBy";
                break;
            case "WASENDEDBY" :
                operator = "wasEndedBy";
                break;
            default:
                throw new IllegalArgumentException(relType+" is not a recognised PROV-N relationship.");
        }
        output.append(operator).append("(");
        if(includeId)
        {
            output.append(r.getId()).append("; ");
        }
        output.append(r.getStartNode().getId()).append(", ").append(r.getEndNode().getId());
        if(includeProperties)
        {
            output.append(", [");
            int i = 0;
            for (String key : r.getPropertyKeys()) {
                if(!key.startsWith("__")) {
                    output.append(key).append("=\"").append(r.getProperty(key)).append("\", ");
                    i++;
                }
            }
            if(i>0)
            {
                output.delete(output.length()-2, output.length()-1);
            }
            output.append("]");
        }
        output.append(")");
        this.relationships.add(output.toString());
    }
}
