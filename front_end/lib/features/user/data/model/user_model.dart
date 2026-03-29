class UserModel {
  final String id;
  final String email;
  final bool isEmailVerified;
  final String status;
  final bool profileCompleted;
  final UserProfile profile;

  UserModel({
    required this.id,
    required this.email,
    required this.isEmailVerified,
    required this.status,
    required this.profileCompleted,
    required this.profile,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'] ?? '',
      email: json['email'] ?? '',
      isEmailVerified: json['is_email_verified'] ?? false,
      status: json['status'] ?? '',
      profileCompleted: json['profile_completed'] ?? false,
      profile: UserProfile.fromJson(json['profile'] ?? {}),
    );
  }
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'is_email_verified': isEmailVerified,
      'status': status,
      'profile_completed': profileCompleted,
      'profile': profile.toJson(), // Gọi tiếp toJson của UserProfile
    };
  }
}

class UserProfile {
  final String id;
  final String fullName;
  final String avatarUrl;
  final String bio;

  UserProfile({
    required this.id,
    required this.fullName,
    required this.avatarUrl,
    required this.bio,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      id: json['id'] ?? '',
      fullName: json['full_name'] ?? '',
      avatarUrl: json['avatar_url'] ?? '',
      bio: json['bio'] ?? '',
    );
  }
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'full_name': fullName,
      'avatar_url': avatarUrl,
      'bio': bio,
    };
  }
}
