using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace chat_service.Migrations
{
    /// <inheritdoc />
    public partial class AddChannelMemberReplica : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropIndex(
                name: "IX_outbox_events_processed_at",
                table: "outbox_events");

            migrationBuilder.DropIndex(
                name: "IX_channel_reads_channel_id_user_id",
                table: "channel_reads");

            migrationBuilder.AlterColumn<int>(
                name: "type",
                table: "messages",
                type: "integer",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "text");

            migrationBuilder.AlterColumn<int>(
                name: "type",
                table: "message_mentions",
                type: "integer",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "text");

            migrationBuilder.AlterColumn<int>(
                name: "type",
                table: "channels_replica",
                type: "integer",
                nullable: false,
                oldClrType: typeof(string),
                oldType: "text");

            migrationBuilder.CreateTable(
                name: "channel_members_replica",
                columns: table => new
                {
                    channel_id = table.Column<Guid>(type: "uuid", nullable: false),
                    user_id = table.Column<Guid>(type: "uuid", nullable: false),
                    joined_at = table.Column<DateTime>(type: "timestamp with time zone", nullable: false, defaultValueSql: "CURRENT_TIMESTAMP")
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_channel_members_replica", x => new { x.channel_id, x.user_id });
                    table.ForeignKey(
                        name: "FK_channel_members_replica_channels_replica_channel_id",
                        column: x => x.channel_id,
                        principalTable: "channels_replica",
                        principalColumn: "id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_channel_reads_channel_id",
                table: "channel_reads",
                column: "channel_id");

            migrationBuilder.CreateIndex(
                name: "IX_channel_members_replica_user_id",
                table: "channel_members_replica",
                column: "user_id");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "channel_members_replica");

            migrationBuilder.DropIndex(
                name: "IX_channel_reads_channel_id",
                table: "channel_reads");

            migrationBuilder.AlterColumn<string>(
                name: "type",
                table: "messages",
                type: "text",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer");

            migrationBuilder.AlterColumn<string>(
                name: "type",
                table: "message_mentions",
                type: "text",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer");

            migrationBuilder.AlterColumn<string>(
                name: "type",
                table: "channels_replica",
                type: "text",
                nullable: false,
                oldClrType: typeof(int),
                oldType: "integer");

            migrationBuilder.CreateIndex(
                name: "IX_outbox_events_processed_at",
                table: "outbox_events",
                column: "processed_at",
                filter: "processed_at IS NULL");

            migrationBuilder.CreateIndex(
                name: "IX_channel_reads_channel_id_user_id",
                table: "channel_reads",
                columns: new[] { "channel_id", "user_id" },
                unique: true);
        }
    }
}
