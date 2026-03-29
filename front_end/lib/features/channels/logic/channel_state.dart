import 'package:equatable/equatable.dart';
import 'package:front_end/features/channels/data/model/channel_model.dart';

abstract class ChannelState extends Equatable {
  const ChannelState();

  @override
  List<Object?> get props => [];
}

class ChannelInitial extends ChannelState {}

class ChannelLoading extends ChannelState {}

// Trạng thái lấy danh sách thành công
class ChannelLoadSuccess extends ChannelState {
  final List<ChannelModel> channels;

  const ChannelLoadSuccess(this.channels);

  @override
  List<Object?> get props => [channels];
}

class ChannelFailure extends ChannelState {
  final String message;

  const ChannelFailure(this.message);

  @override
  List<Object?> get props => [message];
}
