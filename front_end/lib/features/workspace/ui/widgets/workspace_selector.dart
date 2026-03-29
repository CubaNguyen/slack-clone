import 'package:flutter/material.dart';

class WorkspaceSelector extends StatelessWidget {
  const WorkspaceSelector({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                'WORKSPACES',
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey[600],
                  fontWeight: FontWeight.bold,
                ),
              ),
              const Icon(
                Icons.add_circle_outline,
                size: 18,
                color: Colors.purple,
              ),
            ],
          ),
        ),
        SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: Row(
            children: [
              _buildWorkspaceIcon('AT', isSelected: true),
              _buildWorkspaceIcon('DS'),
              _buildWorkspaceIcon('MK'),
              _buildWorkspaceIcon('PX'),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildWorkspaceIcon(String text, {bool isSelected = false}) {
    return Container(
      margin: const EdgeInsets.only(right: 12),
      width: 48,
      height: 48,
      decoration: BoxDecoration(
        color: isSelected ? Colors.white : Colors.grey[200],
        borderRadius: BorderRadius.circular(12),
        border: isSelected ? Border.all(color: Colors.purple, width: 2) : null,
      ),
      alignment: Alignment.center,
      child: Text(
        text,
        style: TextStyle(
          fontWeight: FontWeight.bold,
          color: isSelected ? Colors.purple : Colors.grey[700],
        ),
      ),
    );
  }
}
