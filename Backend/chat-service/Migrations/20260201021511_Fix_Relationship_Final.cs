using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace chat_service.Migrations
{
    /// <inheritdoc />
    public partial class Fix_Relationship_Final : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_messages_channels_replica_ChannelId1",
                table: "messages");

            migrationBuilder.DropForeignKey(
                name: "FK_messages_messages_ParentId1",
                table: "messages");

            migrationBuilder.DropIndex(
                name: "IX_messages_ChannelId1",
                table: "messages");

            migrationBuilder.DropIndex(
                name: "IX_messages_ParentId1",
                table: "messages");

            migrationBuilder.DropColumn(
                name: "ChannelId1",
                table: "messages");

            migrationBuilder.DropColumn(
                name: "ParentId1",
                table: "messages");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "ChannelId1",
                table: "messages",
                type: "uuid",
                nullable: true);

            migrationBuilder.AddColumn<Guid>(
                name: "ParentId1",
                table: "messages",
                type: "uuid",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_messages_ChannelId1",
                table: "messages",
                column: "ChannelId1");

            migrationBuilder.CreateIndex(
                name: "IX_messages_ParentId1",
                table: "messages",
                column: "ParentId1");

            migrationBuilder.AddForeignKey(
                name: "FK_messages_channels_replica_ChannelId1",
                table: "messages",
                column: "ChannelId1",
                principalTable: "channels_replica",
                principalColumn: "id");

            migrationBuilder.AddForeignKey(
                name: "FK_messages_messages_ParentId1",
                table: "messages",
                column: "ParentId1",
                principalTable: "messages",
                principalColumn: "id");
        }
    }
}
