class ChannelModel {
  final String id;
  final String name;
  final String type;
  final bool isDefault;
  final bool isArchived;

  ChannelModel({
    required this.id,
    required this.name,
    required this.type,
    required this.isDefault,
    required this.isArchived,
  });

  factory ChannelModel.fromJson(Map<String, dynamic> json) {
    return ChannelModel(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      type: json['type'] ?? 'PUBLIC', // Mặc định là PUBLIC nếu thiếu
      isDefault: json['isDefault'] ?? false,
      isArchived: json['isArchived'] ?? false,
    );
  }
}
