import 'package:flutter_bloc/flutter_bloc.dart';

import '../data/channel_repository.dart';
import 'channel_state.dart';

class ChannelCubit extends Cubit<ChannelState> {
  final ChannelRepository _repository;

  ChannelCubit(this._repository) : super(ChannelInitial());

  Future<void> fetchChannels(String workspaceId) async {
    emit(ChannelLoading());
    try {
      final channels = await _repository.getChannels(workspaceId);
      // Quăng danh sách ra cho UI
      emit(ChannelLoadSuccess(channels));
    } catch (e) {
      emit(ChannelFailure(e.toString().replaceAll('Exception: ', '')));
    }
  }
}
