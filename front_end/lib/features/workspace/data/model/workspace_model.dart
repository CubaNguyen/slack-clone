class WorkspaceModel {
  final String id;
  final String name;
  final String slug;
  final String role;
  final int memberCount;

  WorkspaceModel({
    required this.id,
    required this.name,
    required this.slug,
    this.role = '',
    this.memberCount = 0,
  });

  factory WorkspaceModel.fromJson(Map<String, dynamic> json) {
    return WorkspaceModel(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      slug: json['slug'] ?? '',
      role: json['role'] ?? '',
      memberCount: json['memberCount'] ?? 0,
    );
  }
}
