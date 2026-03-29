using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace chat_service.Migrations
{
    /// <inheritdoc />
    public partial class Remove_All_Shadow_Cols : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_message_pins_channels_replica_ChannelId1",
                table: "message_pins");

            migrationBuilder.DropForeignKey(
                name: "FK_scheduled_messages_channels_replica_ChannelId1",
                table: "scheduled_messages");

            migrationBuilder.DropIndex(
                name: "IX_scheduled_messages_ChannelId1",
                table: "scheduled_messages");

            migrationBuilder.DropIndex(
                name: "IX_message_pins_ChannelId1",
                table: "message_pins");

            migrationBuilder.DropColumn(
                name: "ChannelId1",
                table: "scheduled_messages");

            migrationBuilder.DropColumn(
                name: "ChannelId1",
                table: "message_pins");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "ChannelId1",
                table: "scheduled_messages",
                type: "uuid",
                nullable: true);

            migrationBuilder.AddColumn<Guid>(
                name: "ChannelId1",
                table: "message_pins",
                type: "uuid",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_scheduled_messages_ChannelId1",
                table: "scheduled_messages",
                column: "ChannelId1");

            migrationBuilder.CreateIndex(
                name: "IX_message_pins_ChannelId1",
                table: "message_pins",
                column: "ChannelId1");

            migrationBuilder.AddForeignKey(
                name: "FK_message_pins_channels_replica_ChannelId1",
                table: "message_pins",
                column: "ChannelId1",
                principalTable: "channels_replica",
                principalColumn: "id");

            migrationBuilder.AddForeignKey(
                name: "FK_scheduled_messages_channels_replica_ChannelId1",
                table: "scheduled_messages",
                column: "ChannelId1",
                principalTable: "channels_replica",
                principalColumn: "id");
        }
    }
}
