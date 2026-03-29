import 'package:flutter/material.dart';
import 'package:front_end/features/profile/ui/widgets/profile_header.dart';
import 'package:front_end/features/profile/ui/widgets/profile_info_card.dart';
import 'package:front_end/features/profile/ui/widgets/quick_action_card.dart';
import 'package:front_end/features/profile/ui/widgets/settings_section.dart';
import 'package:front_end/features/profile/ui/widgets/settings_tile.dart';
import 'package:front_end/features/profile/ui/widgets/sign_out_button.dart';
// Import file widgets vừa tạo ở trên

class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Column(
          children: [
            const ProfileHeader(),
            Expanded(
              child: ListView(
                padding: const EdgeInsets.symmetric(horizontal: 16),
                children: const [
                  ProfileInfoCard(),
                  SizedBox(height: 16),
                  QuickActionCard(
                    icon: Icons.edit_note,
                    title: 'Update status',
                    subtitle: 'Tell your team what\'s up',
                  ),
                  QuickActionCard(
                    icon: Icons.notifications_none,
                    title: 'Pause notifications',
                    subtitle: 'Active for 1 hour',
                  ),
                  SettingsSection(
                    title: 'Account Settings',
                    children: [
                      SettingsTile(
                        icon: Icons.notifications_none,
                        title: 'Notifications',
                      ),
                      SettingsTile(
                        icon: Icons.person_outline,
                        title: 'Account',
                      ),
                      SettingsTile(
                        icon: Icons.settings_outlined,
                        title: 'Workspace settings',
                        isPro: true,
                      ),
                    ],
                  ),
                  SettingsSection(
                    title: 'App Information',
                    children: [
                      SettingsTile(
                        icon: Icons.info_outline,
                        title: 'About',
                        value: 'v4.2.0',
                      ),
                    ],
                  ),
                  SignOutButton(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
