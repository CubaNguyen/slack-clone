using System.Data;
using Microsoft.Data.SqlClient; // Thư viện kết nối SQL Server
using Microsoft.Extensions.Configuration;
using Application.Common.Interfaces;

namespace Infrastructure.Persistence;

public class SqlConnectionFactory : ISqlConnectionFactory
{
    private readonly IConfiguration _configuration;

    public SqlConnectionFactory(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public IDbConnection CreateConnection()
    {
        // Lấy chuỗi kết nối từ appsettings.json
        return new SqlConnection(_configuration.GetConnectionString("DefaultConnection"));
    }
}