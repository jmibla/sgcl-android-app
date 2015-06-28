package project_sgcl.sgclapp;

import java.util.*;

/**
 * User: Joaquín & María * Date: 10/03/15
 * Time: 15:58
 */

public class Product
{
    private int id;
    private String code;
    private String name;
    private String description;
    private String changeHistory;
    private int numberConsumerUnit;
    private String creationDate;
    private String lastModificationDate;



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getChangeHistory()
    {
        return changeHistory;
    }

    public void setChangeHistory(String changeHistory)
    {
        this.changeHistory = changeHistory;
    }

    public int getNumberConsumerUnit()
    {
        return numberConsumerUnit;
    }

    public void setNumberConsumerUnit(int numberConsumerUnit) {
        this.numberConsumerUnit = numberConsumerUnit;
    }

    public String getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(String creationDate)
    {
        this.creationDate = creationDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }
}
