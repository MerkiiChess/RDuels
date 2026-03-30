package ru.merkii.rduels.factory;

import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import ru.merkii.rduels.config.category.CategoryItemConfiguration;
import ru.merkii.rduels.config.enchant.EnchantItemConfiguration;
import ru.merkii.rduels.config.menu.MenuConfiguration;
import ru.merkii.rduels.config.messages.MessageConfig;
import ru.merkii.rduels.core.arena.api.ArenaAPI;
import ru.merkii.rduels.core.customkit.api.CustomKitAPI;
import ru.merkii.rduels.core.customkit.config.CustomKitConfiguration;
import ru.merkii.rduels.core.customkit.storage.CustomKitStorage;
import ru.merkii.rduels.core.duel.api.DuelAPI;
import ru.merkii.rduels.core.duel.config.DuelConfiguration;
import ru.merkii.rduels.core.duel.matchmaking.DuelMatchmakingService;
import ru.merkii.rduels.core.party.api.PartyAPI;
import ru.merkii.rduels.gui.click.*;
import ru.merkii.rduels.gui.extractor.*;
import ru.merkii.rduels.gui.internal.InventoryGUIFactory;
import ru.merkii.rduels.gui.internal.click.ClickHandlerRegistry;
import ru.merkii.rduels.gui.internal.extractor.ValueExtractorRegistry;
import ru.merkii.rduels.gui.internal.paged.PageResolverRegistry;
import ru.merkii.rduels.gui.page.*;
import ru.merkii.rduels.gui.provider.BaseInventoryFactory;
import ru.merkii.rduels.gui.provider.click.RootClickHandlerFactory;

@Factory
public class GUIFactory {

    @Bean
    public InventoryGUIFactory guiFactory(MenuConfiguration config, PageResolverRegistry pageResolverRegistry,
                                          ValueExtractorRegistry valueExtractorRegistry) {
        return new BaseInventoryFactory(config, valueExtractorRegistry, pageResolverRegistry);
    }

    @Bean
    public SelectKitClickHandler selectKitClickHandler(MenuConfiguration config) {
        return new SelectKitClickHandler(config);
    }

    @Bean
    public OpenCategoryClickHandler openCategoryClickHandler(MenuConfiguration config, InventoryGUIFactory factory, CategoryItemConfiguration categoryItemConfiguration) {
        return new OpenCategoryClickHandler(config, factory, categoryItemConfiguration);
    }

    @Bean
    public EditEnchantOrAmountClickHandler editEnchantOrAmountClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        return new EditEnchantOrAmountClickHandler(config, factory);
    }

    @Bean
    public RemoveItemClickHandler removeItemClickHandler(MenuConfiguration config) {
        return new RemoveItemClickHandler(config);
    }

    @Bean
    public SetAmountClickHandler setAmountClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        return new SetAmountClickHandler(config, factory);
    }

    @Bean
    public ApplyEnchantClickHandler applyEnchantClickHandler(MenuConfiguration config, InventoryGUIFactory factory) {
        return new ApplyEnchantClickHandler(config, factory);
    }

    @Bean
    public SelectCustomKitClickHandler selectCustomKitClickHandler() {
        return new SelectCustomKitClickHandler();
    }

    @Bean
    public SelectServerKitClickHandler selectServerKitClickHandler() {
        return new SelectServerKitClickHandler();
    }

    @Bean
    public SelectOptionClickHandler selectOptionClickHandler(MenuConfiguration config) {
        return new SelectOptionClickHandler(config);
    }

    @Bean
    public ChallengePartyClickHandler challengePartyClickHandler(MenuConfiguration config) {
        return new ChallengePartyClickHandler(config);
    }

    @Bean
    public ExitMenuClickHandler exitMenuClickHandler(MenuConfiguration config) {
        return new ExitMenuClickHandler(config);
    }

    @Bean
    public SelectCategoryClickHandler selectCategoryClickHandler(CustomKitStorage customKitStorage, InventoryGUIFactory inventoryGUIFactory, MenuConfiguration configuration) {
        return new SelectCategoryClickHandler(customKitStorage, inventoryGUIFactory, configuration);
    }

    @Bean
    public ChoiceItemClickHandler choiceItemClickHandler(InventoryGUIFactory inventoryGUIFactory) {
        return new ChoiceItemClickHandler(inventoryGUIFactory);
    }

    @Bean
    public RequestFightClickHandler requestFightClickHandler(CustomKitAPI customKitAPI, DuelAPI duelAPI, ArenaAPI arenaAPI, MessageConfig messageConfig) {
        return new RequestFightClickHandler(customKitAPI, duelAPI, arenaAPI, messageConfig);
    }

    @Bean
    public MatchmakingSelectKitClickHandler matchmakingSelectKitClickHandler(DuelMatchmakingService duelMatchmakingService, ArenaAPI arenaAPI, DuelAPI duelAPI, MessageConfig messageConfig) {
        return new MatchmakingSelectKitClickHandler(duelMatchmakingService, arenaAPI, duelAPI, messageConfig);
    }

    @Bean
    public ToggleKitTypeClickHandler toggleKitTypeClickHandler(InventoryGUIFactory factory) {
        return new ToggleKitTypeClickHandler(factory);
    }

    @Bean
    public ClickHandlerRegistry clickHandlerRegistry(InventoryGUIFactory factory,
                                                     SelectKitClickHandler selectKitClickHandler,
                                                     OpenCategoryClickHandler openCategoryClickHandler,
                                                     EditEnchantOrAmountClickHandler editEnchantOrAmountClickHandler,
                                                     RemoveItemClickHandler removeItemClickHandler,
                                                     SetAmountClickHandler setAmountClickHandler,
                                                     ApplyEnchantClickHandler applyEnchantClickHandler,
                                                     SelectCustomKitClickHandler selectCustomKitClickHandler,
                                                     SelectServerKitClickHandler selectServerKitClickHandler,
                                                     SelectOptionClickHandler selectOptionClickHandler,
                                                     ChallengePartyClickHandler challengePartyClickHandler,
                                                     ExitMenuClickHandler exitMenuClickHandler,
                                                     SelectCategoryClickHandler selectCategoryClickHandler,
                                                     ChoiceItemClickHandler choiceItemClickHandler,
                                                     RequestFightClickHandler requestFightClickHandler,
                                                     MatchmakingSelectKitClickHandler matchmakingSelectKitClickHandler,
                                                     ToggleKitTypeClickHandler toggleKitTypeClickHandler) {

        ClickHandlerRegistry clickHandlerRegistry = new ClickHandlerRegistry();

        clickHandlerRegistry.register("root", new RootClickHandlerFactory());
        clickHandlerRegistry.register(RequestFightClickHandler.NAME, requestFightClickHandler);
        clickHandlerRegistry.register("DUEL_REQUEST_ARENA_OPTION", new DuelRequestOptionClickHandler(factory, "arena"));
        clickHandlerRegistry.register("DUEL_REQUEST_KIT_OPTION", new DuelRequestOptionClickHandler(factory, "kit"));
        clickHandlerRegistry.register("DUEL_REQUEST_NUM_GAMES_OPTION", new DuelRequestOptionClickHandler(factory, "num_games"));
        clickHandlerRegistry.register(SelectKitClickHandler.NAME, selectKitClickHandler);
        clickHandlerRegistry.register(OpenCategoryClickHandler.NAME, openCategoryClickHandler);
        clickHandlerRegistry.register(EditEnchantOrAmountClickHandler.NAME, editEnchantOrAmountClickHandler);
        clickHandlerRegistry.register(RemoveItemClickHandler.NAME, removeItemClickHandler);
        clickHandlerRegistry.register(SetAmountClickHandler.NAME, setAmountClickHandler);
        clickHandlerRegistry.register(ApplyEnchantClickHandler.NAME, applyEnchantClickHandler);
        clickHandlerRegistry.register(SelectCustomKitClickHandler.NAME, selectCustomKitClickHandler);
        clickHandlerRegistry.register(SelectServerKitClickHandler.NAME, selectServerKitClickHandler);
        clickHandlerRegistry.register(SelectOptionClickHandler.NAME, selectOptionClickHandler);
        clickHandlerRegistry.register(ChallengePartyClickHandler.NAME, challengePartyClickHandler);
        clickHandlerRegistry.register(ExitMenuClickHandler.NAME, exitMenuClickHandler);
        clickHandlerRegistry.register(SelectCategoryClickHandler.NAME, selectCategoryClickHandler);
        clickHandlerRegistry.register(ChoiceItemClickHandler.NAME, choiceItemClickHandler);
        clickHandlerRegistry.register(MatchmakingSelectKitClickHandler.NAME, matchmakingSelectKitClickHandler);
        clickHandlerRegistry.register(ToggleKitTypeClickHandler.NAME, toggleKitTypeClickHandler);

        return clickHandlerRegistry;
    }

    @Bean
    public PageResolverRegistry pageResolverRegistry(PartyAPI partyAPI, DuelConfiguration duelConfiguration, CustomKitConfiguration customKitConfiguration, MenuConfiguration config, CustomKitStorage customKitStorage, CategoryItemConfiguration categoryItemConfiguration, EnchantItemConfiguration enchantItemConfiguration) {
        PageResolverRegistry pageResolverRegistry = new PageResolverRegistry();

        pageResolverRegistry.register("CATEGORIES", new CategoriesPageResolver(categoryItemConfiguration));
        pageResolverRegistry.register("KIT_LIST", new KitListPageResolver(config.settings().createSettings()));
        pageResolverRegistry.register("KIT_SLOTS", new KitSlotsPageResolver(customKitStorage));
        pageResolverRegistry.register("ENCHANT_CATEGORIES", new EnchantCategoriesPageResolver(enchantItemConfiguration));
        pageResolverRegistry.register("AMOUNT_OPTIONS", new AmountOptionsPageResolver(customKitConfiguration));
        pageResolverRegistry.register("REQUEST_OPTIONS", new RequestOptionsPageResolver(duelConfiguration.choiceKitMenu()));
        pageResolverRegistry.register("PARTY_LIST", new PartyListPageResolver(partyAPI));
        pageResolverRegistry.register("CHOICE_ITEM_CATEGORY", new ChoiceItemCategoryPageResolver());

        return pageResolverRegistry;
    }

    @Bean
    public ValueExtractorRegistry valueExtractorRegistry(CustomKitAPI customKitAPI, MenuConfiguration config, PartyAPI partyAPI) {
        ValueExtractorRegistry valueExtractorRegistry = new ValueExtractorRegistry();

        valueExtractorRegistry.register(new DuelOptionValueExtractor());
        valueExtractorRegistry.register(new ChoiceItemCategoryValueExtractor());
        valueExtractorRegistry.register(new KitSelectedValueExtractor(config.messages(), customKitAPI));
        valueExtractorRegistry.register(new SlotValueExtractor());
        valueExtractorRegistry.register(new PartyValueExtractor(config, partyAPI));
        valueExtractorRegistry.register(new CategoryValueExtractor());

        return valueExtractorRegistry;
    }

}
