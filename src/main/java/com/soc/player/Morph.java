package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.SocWars;
import com.soc.networking.ModPacketCodecs;
import com.soc.util.ModCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public record Morph(BlockState blockState, Box boundingBox, float health) {
	public static final Codec<Morph> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			BlockState.CODEC.fieldOf("block_state").orElse(null).forGetter(Morph::blockState),
			ModCodecs.BOX.fieldOf("bounding_box").orElse(VoxelShapes.fullCube().getBoundingBox()).forGetter(Morph::boundingBox),
			Codecs.POSITIVE_FLOAT.fieldOf("health").orElse(20f).forGetter(Morph::health)
	).apply(instance, Morph::new));

	public static final PacketCodec<ByteBuf, Morph> PACKET_CODEC = PacketCodec.tuple(
			ModPacketCodecs.BLOCK_STATE, Morph::blockState,
			ModPacketCodecs.BOX, Morph::boundingBox,
			PacketCodecs.FLOAT, Morph::health,
			Morph::new
	);

	public static final Identifier HEALTH_MODIFIER_ID = Identifier.of(SocWars.MOD_ID, "morph_health");

	@Nullable
	public static Morph of(BlockState blockState, BlockView world) {
		return blockState == null ? null : new Morph(blockState, makeBoundingBox(blockState, world), calculateMaxHealth(blockState, world));
	}

	public boolean hasProperty(Property<?> property) {
		return this.blockState.contains(property);
	}

	private static Box makeBoundingBox(BlockState blockState, BlockView world) {
		final VoxelShape shape = getShape(blockState, world);
		return shape.getBoundingBox().stretch(0d, -shape.getMin(Direction.Axis.Y), 0d);
	}

	private static float calculateMaxHealth(BlockState blockState, BlockView world) {
		final VoxelShape shape = getShape(blockState, world);
		final double volume = shape.getBoundingBoxes().stream().map(Morph::getBoxVolume).reduce(0d, Double::sum);
		return (float)Math.clamp(Math.sqrt(volume) * 20f, 2f, 40f);
	}

	private static VoxelShape getShape(BlockState blockState, BlockView world) {
		final VoxelShape baseShape = blockState.getCollisionShape(world, null);
		return baseShape.isEmpty() ? blockState.getOutlineShape(world, null) : baseShape;
	}

	private static double getBoxVolume(Box box) {
		return box.getLengthX() * box.getLengthY() * box.getLengthZ();
	}
}