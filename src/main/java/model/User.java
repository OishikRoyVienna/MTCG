package model;

public class User
{
    private String username;
    private String password;
    private String token;


    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
        this.token = null;  // Token will be generated later during login
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }
}