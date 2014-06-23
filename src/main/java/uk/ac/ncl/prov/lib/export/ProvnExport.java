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
            writer.write("    prefix ex <http://example.org/>"+System.getProperty("line.separator")+System.getProperty("line.separator"));
            for(String n : this.nodes)
            {
                writer.write("    " + n + System.getProperty("line.separator"));
            }
            writer.write(System.getProperty("line.separator"));
            for(String r : this.relationships)
            {
                writer.write("    "+r+System.getProperty("line.separator"));
            }
            writer.write("enddocument"+System.getProperty("line.separator"));
        }
    }

    @Override
    public void serializeNode(Node n)
    {
        String output;
        String operator = "";
        Boolean collection = false;
        for(Label l : n.getLabels())
        {
            switch (l.name())
            {
                case "Activity" :
                    operator = "activity";
                    break;
                case "Agent" :
                    operator = "agent";
                    break;
                case "Entity" :
                    operator = "entity";
                    collection = n.hasRelationship(DynamicRelationshipType.withName("HADMEMBER"), Direction.OUTGOING);
                    break;
                default:
                    throw new IllegalArgumentException(l.name()+" is not a recognised PROV-N type.");
            }
        }
        output = operator+"("+n.getId()+", ";
        output += "[";
        for(String key : n.getPropertyKeys())
        {
            output += key+"=\""+n.getProperty(key)+"\", ";
        }
        output += (collection)?"prov:type=\"prov:Collection\", ":"";
        output = output.substring(0, output.length() -2);
        output += "])";
        this.nodes.add(output);
    }

    @Override
    public void serializeRelationship(Relationship r) {
        String output;
        String operator;
        String relType = r.getType().name();
        Boolean includeId = true;
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
            default:
                throw new IllegalArgumentException(relType+" is not a recognised PROV-N relationship.");
        }
        output = operator+"(";
        output += (includeId)?r.getId()+"; ":"";
        output += r.getStartNode().getId()+", "+r.getEndNode().getId()+")";
        this.relationships.add(output);
    }
}
